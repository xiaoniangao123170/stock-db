package com.ming.stock.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.ming.stock.pojo.domain.InnerMarketDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * 大盘行情的两级缓存：
 *
 * L1：Caffeine（JVM 本地，零网络 IO）
 * L2：Redis（多实例共享，TTL 兜底）
 * DB：MySQL（最终回源）
 *
 * 读：L1 -> L2 -> DB；L2 命中会回填 L1，DB 回源会回填 L1 + L2。
 * 失效：MQ 消费后同时删 L1 和 L2，再调用查询重新预热。
 */
@Service
@Slf4j
public class InnerMarketCacheService {

    /** L1 key，保持和原代码一致，避免影响已有调用 */
    public static final String L1_KEY = "innerMarketKey";

    /** L2 key，独立命名，避免和验证码/股票代码缓存冲突 */
    public static final String L2_KEY = "market:inner:latest";

    /** L2 TTL，MQ 失效失败时的兜底 */
    private static final Duration L2_TTL = Duration.ofMinutes(2);

    @Autowired
    private Cache<String, Object> caffeineCache;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 两级缓存读取。
     *
     * Caffeine 的 get(key, loader) 是原子的：同一个 JVM 内同一个 key
     * 只有一个线程执行 loader，其他线程等待结果，避免本地缓存击穿。
     */
    @SuppressWarnings("unchecked")
    public List<InnerMarketDomain> get(Supplier<List<InnerMarketDomain>> dbLoader) {
        Object value = caffeineCache.get(L1_KEY, key -> loadThroughRedis(dbLoader));
        return (List<InnerMarketDomain>) value;
    }

    /**
     * L1 未命中：先查 L2，L2 没有才回源 DB。
     */
    private List<InnerMarketDomain> loadThroughRedis(Supplier<List<InnerMarketDomain>> dbLoader) {
        List<InnerMarketDomain> data = readRedis();
        if (data != null) {
            return data;
        }

        data = dbLoader.get();
        if (data == null) {
            data = List.of();
        }
        writeRedis(data);
        return data;
    }

    private List<InnerMarketDomain> readRedis() {
        try {
            String json = stringRedisTemplate.opsForValue().get(L2_KEY);
            if (json == null || json.isBlank()) {
                return null;
            }
            return objectMapper.readValue(
                    json,
                    new TypeReference<List<InnerMarketDomain>>() {
                    });
        } catch (Exception e) {
            // Redis 异常时降级查 DB，不要让查询接口整体失败
            log.warn("读取大盘 Redis 二级缓存失败，降级查库: {}", e.getMessage());
            return null;
        }
    }

    private void writeRedis(List<InnerMarketDomain> data) {
        try {
            stringRedisTemplate.opsForValue().set(
                    L2_KEY,
                    objectMapper.writeValueAsString(data),
                    L2_TTL);
        } catch (Exception e) {
            // Redis 写失败时只保留 L1，不影响本次查询
            log.warn("写入大盘 Redis 二级缓存失败，仅保留本地缓存: {}", e.getMessage());
        }
    }

    /**
     * MQ 消费后调用：同时失效 L1 + L2。
     * 调用方随后执行一次查询，即可把新数据重新预热到两层。
     */
    public void evict() {
        caffeineCache.invalidate(L1_KEY);
        try {
            stringRedisTemplate.delete(L2_KEY);
        } catch (Exception e) {
            log.warn("删除大盘 Redis 二级缓存失败: {}", e.getMessage());
        }
    }
}
