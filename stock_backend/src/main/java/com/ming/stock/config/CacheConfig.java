package com.ming.stock.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Ming
 * @Description TODO
 */
@Configuration
public class CacheConfig {
    /**
     * 配置RedisTemplate bean 自定义数据的序列化方式 避免使用默认jdk序列化方式
     * jdk序列化方式问题:1.阅读体验很差 2.序列化之后内容体积比较大 占用过多的内存
     * @param redisConnectionFactory  连接reids的工程 底层有依赖启动的时候自动加载
     * @return  要保证bean的名称必须是redisTemplate 否则场景依赖会自动装配相同类型的bean出现多个
     */
    @Bean
    public RedisTemplate redisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory){
        //1.构建RedisTemplate模板对象
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //2.为不同的数据结构设置不同的序列化方案
        //设置key序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        //设置value序列化方式
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        //设置hash中的field字段的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        //设置hash中的value的序列化方式
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        //3.初始话参数设置
        template.afterPropertiesSet();
        return template;
    }
    /**
     * 构建缓存bean
     */
    @Bean
    public Cache<String,Object> caffeineCache(){
        Cache<String,Object> cache = Caffeine
                .newBuilder()
                .maximumSize(200) //设置缓存数量的上线
                //.expireAfterAccess(1, TimeUnit.SECONDS) 访问1秒后删除
                .expireAfterWrite(90, TimeUnit.SECONDS) //L1 TTL 兜底，防止 MQ 丢失导致长期脏数据
                .initialCapacity(100) //初始化缓存空间的大小
                .recordStats() //开始统计
                .build();
        return cache;
    }
}
