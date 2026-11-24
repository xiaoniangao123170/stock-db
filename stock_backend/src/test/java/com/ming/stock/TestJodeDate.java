//package com.ming.stock;
//
//import org.joda.time.DateTime;
//import org.junit.jupiter.api.Test;
//
///**
// * @Author: Ming
// * @Description TODO
// */
//public class TestJodeDate {
//    @Test
//    public void testJode(){
//        //获取指定时间下最近的股票有效交易时间
//        //DateTime.now()获取当前时间 withDate拨动时间 周天
//        DateTime target = DateTime.now().withDate(2022,1,9);
//        //周六
//        target = DateTime.now().withDate(2022,1,8);
//        System.out.println(target);
//        //周一上午九点
//        target =DateTime.now().withDate(2022,1,10).withHourOfDay(9).withMinuteOfHour(0);
//        System.out.println(target);
//        //开盘时间 上午9:30到11:30
//        target=DateTime.now().withDate(2022,1,10).withHourOfDay(9).withMinuteOfHour(30);
//        System.out.println(target);
//        //休盘时间 11:30-13:00
//        target = DateTime.now().withDate(2022,1,10).withHourOfDay(11).withMinuteOfHour(30);
//    }
//}
