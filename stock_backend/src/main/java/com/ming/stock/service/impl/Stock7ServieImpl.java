package com.ming.stock.service.impl;

import com.ming.stock.mapper.StockBusinessMapper;
import com.ming.stock.mapper.StockOuterMarketIndexInfoMapper;
import com.ming.stock.mapper.StockRtInfoMapper;
import com.ming.stock.pojo.domain.*;
import com.ming.stock.service.Stock7Service;
import com.ming.stock.utils.DateTimeUtil;
import com.ming.stock.vo.resp.R;
import com.ming.stock.vo.resp.ResponseCode;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Ming
 * @Description TODO
 */
@Service
public class Stock7ServieImpl implements Stock7Service {
    @Autowired
    private StockOuterMarketIndexInfoMapper stockOuterMarketIndexInfoMapper;
    @Autowired
    private StockBusinessMapper stockBusinessMapper;
    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;
    /**
     * 获取外盘数据
     * @return
     */
    @Override
    public R<List<OutMarketDomain>> getOutMarket() {
        // 获取当前时间点
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        // 制造mock数据
        curDate = DateTime.parse("2024-12-01 10:57:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        // 从数据库中查询数据
        List<OutMarketDomain> data = stockOuterMarketIndexInfoMapper.getOutMarket(curDate);

        // 封装数据
        return R.ok(data);


    }


    /**
     * 股票Code联想推荐功能
     * @param searchStr
     * @return
     */
    @Override
    public R<List<CodeSearchDomain>> stockCodeSearch(String searchStr) {
        // 获取所有股票信息
        List<CodeSearchDomain> businessMsg =  stockBusinessMapper.getMsg(searchStr);
        return R.ok(businessMsg);


    }

    /**
     * 个股描述功能实现
     * @param code
     * @return
     */
    @Override
    public R<StockDescribeDomain> getStockDescribe(String code) {
        //检查参数校验
        if(StringUtils.isBlank(code)){
            R.error(ResponseCode.DATA_ERROR.getMessage());
        }
        // 从数据库中获取数据
        StockDescribeDomain data = stockBusinessMapper.getStockDescribe(code);
        return R.ok(data);
    }
    /**
     * 功能描述：统计每周内的股票数据信息，信息包含：
     * 股票ID、 一周内最高价、 一周内最低价 、周1开盘价、周5的收盘价、
     * 整周均价、以及一周内最大交易日期（一般是周五所对应日期）
     *
     * @param code
     * @return
     */
    @Override
    public R<List<WeekLineDomain>> getWeekLine(String code) {
        // 获取当前时间点
        DateTime curDate = DateTimeUtil.getLastDate4Stock(DateTime.now());
        // 制造mock数据
        curDate = DateTime.parse("2024-12-30 09:42:00",
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));

        Date endDate = curDate.toDate();
        Date startDate = curDate.minusMonths(3).toDate();

        // 分步骤查询：多语句SQL在分库分表环境下不可用，拆分为3条独立SQL
        List<WeekLineDomain> baseList  = stockRtInfoMapper.getWeekBase(startDate, endDate, code);
        List<WeekLineDomain> closeList = stockRtInfoMapper.getWeekClose(startDate, endDate, code);
        List<WeekLineDomain> openList  = stockRtInfoMapper.getWeekOpen(startDate, endDate, code);

        // 以 mxTime（当周最后交易日时间）作为关联键，建立索引
        Map<Date, WeekLineDomain> map = new HashMap<>();
        for (WeekLineDomain base : baseList) {
            map.put(base.getMxTime(), base);
        }

        // 合并每周收盘价
        for (WeekLineDomain d : closeList) {
            WeekLineDomain base = map.get(d.getMxTime());
            if (base != null) {
                base.setClosePrice(d.getClosePrice());
            }
        }

        // 合并每周开盘价
        for (WeekLineDomain d : openList) {
            WeekLineDomain base = map.get(d.getMxTime());
            if (base != null) {
                base.setOpenPrice(d.getOpenPrice());
            }
        }

        return R.ok(new ArrayList<>(map.values()));
    }
    /**
     * 获取个股最新分时行情数据，主要包含：
     * 	开盘价、前收盘价、最新价、最高价、最低价、成交金额和成交量、交易时间信息
     * @param code
     * @return
     */
    @Override
    public R<MarketDetailDomain> getMarketDetail(String code) {
        // 获取最新时间
        DateTime curDate = DateTimeUtil.getLastDate4Stock(DateTime.now());
        // 制造mock数据
        curDate = DateTime.parse("2024-12-30 09:42:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date date = curDate.toDate();

        // 查询数据
        MarketDetailDomain msg = stockRtInfoMapper.getMarketDetail(date , code);

        // 封装数据
        return R.ok(msg);


    }
    /**
     * 个股交易流水行情数据查询--查询最新交易流水，按照交易时间降序取前10
     * @param code
     * @return
     */
    @Override
    public R<List<Map<String, Object>>> getStockTradinStatement(String code) {
        if(StringUtils.isBlank(code)) {
            return R.error(ResponseCode.DATA_ERROR.getMessage());
        }
        List<Map<String,Object>> mapResult=stockRtInfoMapper.getByCodeInfo(code);
        return R.ok(mapResult);
    }


}
