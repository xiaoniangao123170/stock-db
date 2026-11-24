package com.ming.stock.service.impl;

import com.google.common.collect.Lists;
import com.ming.stock.face.StockCacheFace;
import com.ming.stock.mapper.*;
import com.ming.stock.pojo.entity.StockBlockRtInfo;
import com.ming.stock.pojo.entity.StockMarketIndexInfo;
import com.ming.stock.pojo.entity.StockOuterMarketIndexInfo;
import com.ming.stock.pojo.entity.StockRtInfo;
import com.ming.stock.pojo.vo.StockInfoConfig;
import com.ming.stock.service.StockTimerTaskService;
import com.ming.stock.utils.DateTimeUtil;
import com.ming.stock.utils.IdWorker;
import com.ming.stock.utils.ParseType;
import com.ming.stock.utils.ParserStockInfoUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: Ming
 * @Description TODO
 */
@Service
@Slf4j
public class StockTimerTaskServiceImpl implements StockTimerTaskService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StockInfoConfig stockInfoConfig;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;
    @Autowired
    private StockBusinessMapper stockBusinessMapper;
    @Autowired
    private ParserStockInfoUtil parserStockInfoUtil;
    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;
    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private StockCacheFace stockCacheFace;
    @Autowired
    private StockOuterMarketIndexInfoMapper stockOuterMarketIndexInfoMapper;
    //必须保证该对象无状态
    private HttpEntity<Object> httpEntity;

    @Override
    public void getInnerMarketInfo() {

        //1.采集原始数据
        //1.组装url地址
        //String url = "http://hq.sinajs.cn/list=sh000001,sz399001";
        String url = stockInfoConfig.getMarketUrl() + String.join(",", stockInfoConfig.getInner());
        //1.2维护请求头 添加防盗链和用户标识
//        HttpHeaders headers = new HttpHeaders();
//        //防盗链
//        headers.add("Referer","https://finance.sina.com.cn/stock/");
//        //用户客户端标识
//        headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36");
//        //维护http请求实体对象
//        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        //发送请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        //状态码
        int statusCodeValue = responseEntity.getStatusCodeValue();
        if (statusCodeValue != 200) {
            //当前请求失败
            log.error("当前时间点:{},采集数据失败,http状态码:{}",
                    DateTime.now().toString("yyyy-MM-dd HH:mm:ss"), statusCodeValue);
            //其他操作:发送邮件 企业微信 钉钉
            //如果错误下面没有必要继续了
            return;
        }
        //获取js格式数据
        String jsData = responseEntity.getBody();
        log.info("当前时间点:{},采集原始数据的内容:{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"), jsData);
        //2.java正则表达式进行解析原始数据
        //String reg = "var hq_str_sh000001="上证指数,3943.8160,3940.0371,3947.9083,3966.3611,3943.8082,0,0,363419039,803920446058,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2026-08-10,11:33:12,00,";";
        //2.1定义正则表达式  var hq_str_精确匹配固定的前缀文本 (.+)第一组捕获 ()代表捕获分组 .+匹配至少出现一次任意字符
        String reg = "var hq_str_(.+)=\"(.+)\";";
        //2.2表达式编辑  Pattern正则模板类 把字符串正则编译成可以复用的匹配规则 提升编译效率 循环的时候不需要重复的解析正则
        Pattern pattern = Pattern.compile(reg);
        //2.3匹配字符串
        Matcher matcher = pattern.matcher(jsData);
        ArrayList<StockMarketIndexInfo> entities = new ArrayList<>();
        //find()向后查找符合正则的匹配片段 找到返回true
        while (matcher.find()) {
            //1.获取大盘的编码 group(1) 标识第一个分组
            String marketCode = matcher.group(1);
            //2.捕获其他信息
            String otherInfo = matcher.group(2);
            //3.将otherInfo字符串以逗号间隔获取大盘的详情信息
            String[] splitArr = otherInfo.split(",");
            //大盘名称
            String marketName = splitArr[0];
            //获取当前大盘的开盘点数
            BigDecimal openPoint = new BigDecimal(splitArr[1]);
            //前收盘点
            BigDecimal preClosePoint = new BigDecimal(splitArr[2]);
            //当前点
            BigDecimal curPoint = new BigDecimal(splitArr[3]);
            //最高点
            BigDecimal maxPoint = new BigDecimal(splitArr[4]);
            //最低点
            BigDecimal minPoint = new BigDecimal(splitArr[5]);
            //成交量
            Long tradeAmt = Long.valueOf(splitArr[8]);
            //成交金额
            BigDecimal tradeVol = new BigDecimal(splitArr[9]);
            //当前日期时间
            Date curTime = DateTimeUtil.getDateTimeWithoutSecond(splitArr[30] + " " + splitArr[31]).toDate();
            //3.解析好的数据封装entity
            StockMarketIndexInfo info = StockMarketIndexInfo.builder()
                    .id(idWorker.nextId())
                    .marketCode(marketCode)
                    .marketName(marketName)
                    .curPoint(curPoint)
                    .openPoint(openPoint)
                    .preClosePoint(preClosePoint)
                    .maxPoint(maxPoint)
                    .minPoint(minPoint)
                    .tradeVolume(tradeVol)
                    .tradeAmount(tradeAmt)
                    .curTime(curTime)
                    .build();
            entities.add(info);
        }
        log.info("解析数据完毕");
        //4.调用mybatis批量入库
        int count = stockMarketIndexInfoMapper.insertBach(entities);
        if (count > 0) {
            //大盘数据擦剂成功之后 通知backend工程刷新缓存
            //发送日期对象 接收通过接收日期与当前日期比较 能判断出数据的延迟时间 用于运维通知处理
            rabbitTemplate.convertAndSend("stockExchange", "inner.market", new Date());
            log.info("当前时间:{},插入大盘数据:{}成功", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"), entities);
        } else {
            log.error("当前时间:{},插入大盘数据:{}失败", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"), entities);
        }
    }

    /**
     * 定义获取分钟级个股股票数据
     */
    @Override
    public void getStockRtIndex() {
        //1.获取所有个股集合
        List<String> stockIds = stockCacheFace.getAllStockCodeWithPrefix();
        //1.获取所有个股集合
//        List<String> stockIds = stockBusinessMapper.getStockIds();
//        System.out.println(stockIds);
//        //添加大盘业务前缀 sh sz
//        stockIds = stockIds.stream().map(code -> code.startsWith("6") ? "sh" + code : "sz" + code).collect(Collectors.toList());
//        System.out.println(stockIds);
        //一次性将所有的集合拼接到url地址中 导致地址过长参数过多
        //String url = stockInfoConfig.getMarketUrl()+String.join(",",stockIds);
        //核心思路将大的集合切分成若干个小集合 分批次拉取数据
        //将所有的个股编码组成的大集合拆分成若干个小集合  40----->15 15 10
        long startTime = System.currentTimeMillis();
        Lists.partition(stockIds, 15).forEach(codes -> {
            //原始方案
            //分批次采集数据
//            String url = stockInfoConfig.getMarketUrl()+String.join(",",codes);
//            //1.2维护请求头 添加防盗链和用户标识
////            HttpHeaders headers = new HttpHeaders();
////            //防盗链
////            headers.add("Referer","https://finance.sina.com.cn/stock/");
////            //用户客户端标识
////            headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36");
////            //维护http请求实体对象
////            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
//            //发送请求
//            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,httpEntity,String.class);
//            //状态码
//            int statusCodeValue = responseEntity.getStatusCodeValue();
//            if (statusCodeValue!=200){
//                //当前请求失败
//                log.error("当前时间点:{},采集数据失败,http状态码:{}",
//                        DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),statusCodeValue);
//                //其他操作:发送邮件 企业微信 钉钉
//                //如果错误下面没有必要继续了
//                return;
//            }
//            //获取js格式数据
//            String jsData = responseEntity.getBody();
//            //调用工具类解析获取的个股数据
//            List<StockRtInfo> list = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.ASHARE);
//            log.info("采集个股数据:{}",list);
//            //4.调用mybatis批量入库
//            int count = stockRtInfoMapper.insertBach(list);
//            if (count>0){
//                log.info("当前时间:{},插入个股数据:{}成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
//            }else {
//                log.error("当前时间:{},插入个股数据:{}失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
//            }
            //方案1: 原始放完采集个股数据的时候将集合分片 然后分批次是唤醒采集数据 效率不高 存在较高的采集延迟:引入多线程
            //代码的问题:1.每次来一个任务就会创建一个线程 2.如果多线程使用不当会造成cpu的竞争过于激烈 导致频繁的上下文切换 导致程序性能降低
//            new Thread(()->{
//                //分批次采集数据
//            String url = stockInfoConfig.getMarketUrl()+String.join(",",codes);
//            //1.2维护请求头 添加防盗链和用户标识
////            HttpHeaders headers = new HttpHeaders();
////            //防盗链
////            headers.add("Referer","https://finance.sina.com.cn/stock/");
////            //用户客户端标识
////            headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36");
////            //维护http请求实体对象
////            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
//            //发送请求
//            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,httpEntity,String.class);
//            //状态码
//            int statusCodeValue = responseEntity.getStatusCodeValue();
//            if (statusCodeValue!=200){
//                //当前请求失败
//                log.error("当前时间点:{},采集数据失败,http状态码:{}",
//                        DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),statusCodeValue);
//                //其他操作:发送邮件 企业微信 钉钉
//                //如果错误下面没有必要继续了
//                return;
//            }
//            //获取js格式数据
//            String jsData = responseEntity.getBody();
//            //调用工具类解析获取的个股数据
//            List<StockRtInfo> list = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.ASHARE);
//            log.info("采集个股数据:{}",list);
//            //4.调用mybatis批量入库
//            int count = stockRtInfoMapper.insertBach(list);
//            if (count>0){
//                log.info("当前时间:{},插入个股数据:{}成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
//            }else {
//                log.error("当前时间:{},插入个股数据:{}失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
//            }
//            }).start();
            //方案2: 引入线程池的方式
            threadPoolTaskExecutor.execute(() -> {
                //分批次采集数据
                String url = stockInfoConfig.getMarketUrl() + String.join(",", codes);
                //1.2维护请求头 添加防盗链和用户标识
//            HttpHeaders headers = new HttpHeaders();
//            //防盗链
//            headers.add("Referer","https://finance.sina.com.cn/stock/");
//            //用户客户端标识
//            headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36");
//            //维护http请求实体对象
//            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
                //发送请求
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
                //状态码
                int statusCodeValue = responseEntity.getStatusCodeValue();
                if (statusCodeValue != 200) {
                    //当前请求失败
                    log.error("当前时间点:{},采集数据失败,http状态码:{}",
                            DateTime.now().toString("yyyy-MM-dd HH:mm:ss"), statusCodeValue);
                    //其他操作:发送邮件 企业微信 钉钉
                    //如果错误下面没有必要继续了
                    return;
                }
                //获取js格式数据
                String jsData = responseEntity.getBody();
                //调用工具类解析获取的个股数据
                List<StockRtInfo> list = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.ASHARE);
                log.info("采集个股数据:{}", list);
                //4.调用mybatis批量入库
                int count = stockRtInfoMapper.insertBach(list);
                if (count > 0) {
                    log.info("当前时间:{},插入个股数据:{}成功", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"), list);
                } else {
                    log.error("当前时间:{},插入个股数据:{}失败", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"), list);
                }
            });

        });
        long endTime = System.currentTimeMillis();
        long takeTime = endTime - startTime;
        log.info("本次采集花费时间:{}ms", takeTime);
    }

    /**
     * 获取板块实时数据
     * http://vip.stock.finance.sina.com.cn/q/view/newSinaHy.php
     */
    @Override
    public void getStockSectorRtIndex() {
        //发送板块数据请求
        String result = restTemplate.getForObject(stockInfoConfig.getBlockUrl(), String.class);
        //响应结果转板块集合数据
        List<StockBlockRtInfo> infos = parserStockInfoUtil.parse4StockBlock(result);
        log.info("板块数据量：{}", infos.size());
        //行业版类目大概有50个 可以每小时查询一次
        Lists.partition(infos, 20).forEach(list -> {
            threadPoolTaskExecutor.execute(()->{
                stockBlockRtInfoMapper.insertBatch(list);
            });

        });
    }

    /**
     * 外盘数据采集
     */
    @Override
    public void getOutMarket() {
        String url = stockInfoConfig.getMarketUrl() + String.join("," , stockInfoConfig.getOuter());
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        int statusCodeValue = exchange.getStatusCodeValue();
        if (statusCodeValue != 200) {
            log.error("当前时间点{} , 数据采集失败 , 状态码{}", DateTime.now().toString("yyyy-MM-dd HH-mm-ss"), statusCodeValue);
            return;
        }
        String jsData = exchange.getBody();
        List<StockOuterMarketIndexInfo> list = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.OUTER);
        log.info("采集的当前外盘数据：{}", list);

        if (list == null || list.isEmpty()) {
            log.warn("当前时间点{} , 外盘解析结果为空，跳过入库", DateTime.now().toString("yyyy-MM-dd HH-mm-ss"));
            return;
        }

        int num = stockOuterMarketIndexInfoMapper.insertOutMarketData(list);
        if (num > 0) {
            log.info("当前时间点{} , 外盘数据插入成功", DateTime.now().toString("yyyy-MM-dd HH-mm-ss"));
        } else {
            log.error("当前时间点{} , 外盘数据插入失败", DateTime.now().toString("yyyy-MM-dd HH-mm-ss"));
        }


    }


    /**
     * bean生命周期的初始化的回调方法
     */
    @PostConstruct
    public void initData() {
        //1.2维护请求头 添加防盗链和用户标识
        HttpHeaders headers = new HttpHeaders();
        //防盗链
        headers.add("Referer", "https://finance.sina.com.cn/stock/");
        //用户客户端标识
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36");
        //维护http请求实体对象
        httpEntity = new HttpEntity<>(headers);
    }

}
