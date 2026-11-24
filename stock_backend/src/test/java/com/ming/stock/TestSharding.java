//package com.ming.stock;
//
//import com.ming.stock.mapper.StockBlockRtInfoMapper;
//import com.ming.stock.mapper.StockBusinessMapper;
//import com.ming.stock.mapper.StockRtInfoMapper;
//import com.ming.stock.mapper.SysUserMapper;
//import com.ming.stock.pojo.domain.StockBlockDomain;
//import com.ming.stock.pojo.domain.WeekLineDomain;
//import com.ming.stock.pojo.entity.StockBusiness;
//import com.ming.stock.pojo.entity.StockRtInfo;
//import com.ming.stock.pojo.entity.SysUser;
//import com.ming.stock.utils.DateTimeUtil;
//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormat;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@SpringBootTest
//public class TestSharding {
//    @Autowired
//    private SysUserMapper sysUserMapper;
//    @Autowired
//    private StockBusinessMapper stockBusinessMapper;
//    @Autowired
//    private StockBlockRtInfoMapper stockBlockRtInfoMapper;
//    @Autowired
//    private StockRtInfoMapper stockRtInfoMapper;
//    @Test
//    public void testDefault(){
//        SysUser user =sysUserMapper.selectByPrimaryKey(1237361915165020161L);
//    }
//    @Test
//    public void testCastTable(){
////        StockBusiness pojo=StockBusiness.builder()
////                .stockCode("9000")
////                .stockName("9000")
////                .blockName("9000")
////                .business("9000")
////                .blockLabel("9000")
////                .updateTime(new Date())
////                .build();
////        stockBusinessMapper.insert(pojo);
//        stockBusinessMapper.deleteByPrimaryKey(9000L);
//    }
//    @Test
//    public void testCommon4Db(){
//        Date curDate= DateTime.parse("2024-12-30 09:32:00",
//                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
//        List<StockBlockDomain> info=stockBlockRtInfoMapper.sectorAllLimit(curDate);
//        System.out.println(info);
//    }
//    /**
//     * 测试分库分表算法类
//     */
//    @Test
//    public void testShardingDbAndTb() {
//        // 获取当前时间点
//        DateTime curDate = DateTimeUtil.getLastDate4Stock(DateTime.now());
//        // 制造mock数据
//        curDate = DateTime.parse("2024-12-21 09:30:01",
//                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
//
//        Date endDate = curDate.toDate();
//        Date startDate = curDate.minusMonths(3).toDate();
//        String code = "000017";
//
//        // 分步骤查询：多语句SQL在分库分表环境下不可用，拆分为3条独立SQL
//        List<WeekLineDomain> baseList  = stockRtInfoMapper.getWeekBase(startDate, endDate, code);
//        List<WeekLineDomain> closeList = stockRtInfoMapper.getWeekClose(startDate, endDate, code);
//        List<WeekLineDomain> openList  = stockRtInfoMapper.getWeekOpen(startDate, endDate, code);
//
//        // 以 mxTime（当周最后交易日时间）作为关联键，建立索引
//        Map<Date, WeekLineDomain> map = new HashMap<>();
//        for (WeekLineDomain base : baseList) {
//            map.put(base.getMxTime(), base);
//        }
//
//        // 合并每周收盘价
//        for (WeekLineDomain d : closeList) {
//            WeekLineDomain base = map.get(d.getMxTime());
//            if (base != null) {
//                base.setClosePrice(d.getClosePrice());
//            }
//        }
//
//        // 合并每周开盘价
//        for (WeekLineDomain d : openList) {
//            WeekLineDomain base = map.get(d.getMxTime());
//            if (base != null) {
//                base.setOpenPrice(d.getOpenPrice());
//            }
//        }
//    }
//}
