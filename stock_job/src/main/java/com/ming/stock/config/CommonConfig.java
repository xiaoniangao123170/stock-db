package com.ming.stock.config;

import com.ming.stock.pojo.vo.StockInfoConfig;
import com.ming.stock.utils.IdWorker;
import com.ming.stock.utils.ParserStockInfoUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Ming
 * @Description 定义公共的配置类
 */
@Configuration
@EnableConfigurationProperties({StockInfoConfig.class})
public class CommonConfig {

    /**
     * 配置id生成器
     * @return
     */
    @Bean
    public IdWorker idWorker(){
        //参数1:机器id 参数2:机房id 一般是运维人员定唯一行
        return new IdWorker(1L,2L);

    }

    /**
     * 定义解析股票 大盘 外盘 个股 板块相关信息的工具bean
     * @return
     */
    @Bean
    public ParserStockInfoUtil parserStockInfoUtil(){
        return new ParserStockInfoUtil(idWorker());
    }

}
