package com.ming.stock.config;

import com.ming.stock.pojo.vo.TaskThreadPoolInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: Ming
 * @Description 定义线程池的配置类
 */
@Configuration
public class TaskExecutePoolConfig {
    @Autowired
    private TaskThreadPoolInfo info;

    /**
     * 构建线程池的bean对象
     *
     * @return
     */
    @Bean(name = "threadPoolTaskExecutor", destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        //线程池的核心参数 1.核心线程数 2.最大线程数 3.任务队列
        // 4.非核心线程的存活时间 5.时间单位 6.拒绝策略(如果任务太多无法处理给一些处理方式1.抛异常 丢弃) 7.线程工厂(用于创建线程的)
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //设置核心线程数(线程池创建的时候初始化线程数)
        executor.setCorePoolSize(info.getCorePoolSize());
        //最大线程数:只有在缓冲队列满了之后会申请超过核心线程数的线程
        executor.setMaxPoolSize(info.getMaxPoolSize());
        //允许线程的空闲时间:当超过核心线程数之外的线程在空闲时间到达之后被销毁
        executor.setKeepAliveSeconds(info.getKeepAliveSeconds());
        //设置任务队列的长度 缓冲队列 用来缓冲执行任务的队列
        executor.setQueueCapacity(info.getQueueCapacity());
        //设置拒绝策略
        //executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //设置参数初始化
        executor.initialize();
        return executor;
    }
}
