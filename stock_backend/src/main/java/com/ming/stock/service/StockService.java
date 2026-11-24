package com.ming.stock.service;

import com.ming.stock.pojo.domain.*;
import com.ming.stock.vo.resp.PageResult;
import com.ming.stock.vo.resp.R;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * @Author: Ming
 * @Description TODO
 */
public interface StockService {
    /**
     * 获取国内大盘最新数据
     * @return
     */

    R<List<InnerMarketDomain>> getInnerMarketInfo();
    /**
     * 获取国内板块数据 交易金额降序查询
     * @return
     */
    R<List<StockBlockDomain>> sectorAllLimit();
    /**
     * 分页查询股票最新数据 并且按照涨幅排序查询
     * @param page   当前页
     * @param pageSize 每页的大小
     * @return
     */
    R<PageResult<StockUpdownDomain>> getStockPageInfo(Integer page, Integer pageSize);
    /**
     * 统计沪深两市个股最新交易数据，并按涨幅降序排序查询前4条数据
     * @return
     */
    R<List<StockUpdownDomain>> getTopStocksByIncrease();
    /**
     *
     * 统计最新股票交易日内每分钟的涨跌停的股票数量
     * @return
     */
    R<Map<String, List>> getStockUpdownCount();
    /**
     * 将指定也的股票数据导出到excel表中
     * @param page  当前页
     * @param pageSize 每页大小
     * @param response 拦截响应数据
     */
    void exportStockUpdownInfo(Integer page, Integer pageSize, HttpServletResponse response);
    /**
     * 统计国内A股大盘T日和T-1日的成交量对比功能(成交量为沪和深成交量之和)
     * @return
     */
    R<Map<String, List>> getComparedStockTradeAmt();

    /**
     *  统计最新交易时间殿下股票 在各个涨幅区间的数量
     * @return
     */
    R<Map> getIncreaseRangeInfo();
    /**
     * 查询单个股票分时行情数据 如果日期不在有效时间内 则以最近一个股票交易时间作为查询时间点 获取指定股票T日分时数据
     * @param stockCode
     * @return
     */
    R<List<Stock4MinuteDomain>> getStockScreenTimeSharing(String stockCode);
    /**
     * 单个个股日K数据查询 可以根据时间区间查询数日的K线数据
     * @param stockCode 股票编码
     * @return
     */
    R<List<Stock4EvrDayDomain>> getStockScreenDkLine(String stockCode);
}


