package com.ming.stock.service;

import com.ming.stock.pojo.domain.*;
import com.ming.stock.vo.resp.R;

import java.util.List;
import java.util.Map;

/**
 * @Author: Ming
 * @Description TODO
 */
public interface Stock7Service {

    /**
     * 外盘数据展示
     * @return
     */
    R<List<OutMarketDomain>> getOutMarket();

    /**
     * 根据输入的个股代码，进行模糊查询，返回证券代码和证券名称
     * @param searchStr
     * @return
     */
    R<List<CodeSearchDomain>> stockCodeSearch(String searchStr);
    /**
     * 个股主营业务查询接口
     * @param code
     * @return
     */
    R<StockDescribeDomain> getStockDescribe(String code);

    /**
     * 功能描述：统计每周内的股票数据信息，信息包含：
     * 股票ID、 一周内最高价、 一周内最低价 、周1开盘价、周5的收盘价、
     * 整周均价、以及一周内最大交易日期（一般是周五所对应日期）
     *
     * @param code
     * @return
     */
    R<List<WeekLineDomain>> getWeekLine(String code);


    /**
     * 获取个股最新分时行情数据，主要包含：
     * 	开盘价、前收盘价、最新价、最高价、最低价、成交金额和成交量、交易时间信息
     * @param code
     * @return
     */
    R<MarketDetailDomain> getMarketDetail(String code);

    /**
     * 个股交易流水行情数据查询--查询最新交易流水，按照交易时间降序取前10
     * @param code
     * @return
     */
    R<List<Map<String, Object>>> getStockTradinStatement(String code);

}
