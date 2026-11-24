package com.ming.stock.config;

import com.ming.stock.pojo.vo.StockInfoConfig;
import com.ming.stock.utils.IdWorker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author: Ming
 * @Description 定义公共的配置类
 */
@Configuration
@EnableConfigurationProperties({StockInfoConfig.class})
public class CommonConfig {
    /**
     * 密码加密器
     * BCryptPasswordEncoder方法采用了SHA-256对密码进行加密
     *
     * @return
     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//
//    }

    /**
     * 配置id生成器
     * @return
     */
    @Bean
    public IdWorker idWorker(){
        //参数1:机器id 参数2:机房id 一般是运维人员定唯一行
        return new IdWorker(1L,2L);

    }

}
