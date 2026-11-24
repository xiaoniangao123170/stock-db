package com.ming.stock.mapper;

import com.ming.stock.pojo.domain.*;
import com.ming.stock.pojo.entity.StockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author 11519
* @description 针对表【stock_rt_info(个股详情信息表)】的数据库操作Mapper
* @createDate 2026-08-02 09:34:48
* @Entity com.ming.stock.pojo.entity.StockRtInfo
*/
public interface StockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockRtInfo record);

    int insertSelective(StockRtInfo record);

    StockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockRtInfo record);

    int updateByPrimaryKey(StockRtInfo record);

    /**
     * 分页查询股票最新数据 并且按照涨幅排序查询
     * @param curDate 日期时间
     * @return
     */
    List<StockUpdownDomain> getStockPageInfo(@Param("curDate") Date curDate);
    /**
     * 统计沪深两市个股最新交易数据，并按涨幅降序排序查询前4条数据
     * @return
     */
    List<StockUpdownDomain> findTopStocksByIncrease(@Param("curDate")Date curDate);

    /**
     * 统计最新股票交易日内每分钟的涨跌停的股票数量
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param flag      约定1->涨停 0->跌停
     * @return
     */
    List<Map> getStockUpdownCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("flag") int flag);

    /**
     * 统计最新交易时间殿下股票 在各个涨幅区间的数量
     * @param curDate
     * @return
     */
    List<Map> getIncreaseRangeInfo(@Param("curDate") Date curDate);

    /**
     * 根据股票编码查询指定时间内的分时数据
     * @param openDate  开盘时间
     * @param endDate   截至时间
     * @param stockCode 股票编码
     * @return
     */
    List<Stock4MinuteDomain> getStockScreenTimeSharing(
            @Param("openDate") Date openDate, @Param("endDate") Date endDate, @Param("stockCode") String stockCode);

    /**
     * 根据股票编码查询指定时间范围内的日k数据
     * @param startDate  开盘时间
     * @param endDate    截至时间
     * @param stockCode  股票编码
     * @return
     */
    List<Stock4EvrDayDomain> getStockScreenDkLine(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("stockCode") String stockCode);

    /**
     * 获取指定日期范围内收盘时间
     * @param startDate 起始时间
     * @param endDate  结束时间
     * @param stockCode 股票编码
     * @return
     */
    List<Date> getStockScreenDkLine2(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("stockCode") String stockCode);

    /**
     * 获取指定股票在指定日期下的数据
     * @param mxTimes  指定日期的集合
     * @param stockCode 股票编码
     * @return
     */
    List<Stock4EvrDayDomain> getStockScreenDkLine3(
            @Param("mxTimes") List<Date> mxTimes,
            @Param("stockCode") String stockCode);

    /**
     * 批量插入个股数据
     * @param list
     * @return
     */
    int insertBach(@Param("list") List<StockRtInfo> list);

    /**
     * 功能描述：统计每周内的股票数据信息，信息包含：
     * 股票ID、 一周内最高价、 一周内最低价 、周1开盘价、周5的收盘价、
     * 整周均价、以及一周内最大交易日期（一般是周五所对应日期）
     * @param startDate
     * @param endDate
     * @param code
     * @return
     */
    /**
     * 周K线-基础统计：每周最低/最高/均价/当周最后交易时间
     */
    List<WeekLineDomain> getWeekBase(@Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate,
                                     @Param("code") String code);

    /**
     * 周K线-每周最后一个交易日收盘价
     */
    List<WeekLineDomain> getWeekClose(@Param("startDate") Date startDate,
                                      @Param("endDate") Date endDate,
                                      @Param("code") String code);

    /**
     * 周K线-每周首个交易日开盘价
     */
    List<WeekLineDomain> getWeekOpen(@Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate,
                                     @Param("code") String code);
 /**
     * 获取个股最新分时行情数据，主要包含：
     * 	开盘价、前收盘价、最新价、最高价、最低价、成交金额和成交量、交易时间信息
     * @param code
     * @return
     */
    MarketDetailDomain getMarketDetail(@Param("date") Date date, @Param("code") String code);
    /**
     * 个股交易流水行情数据查询--查询最新交易流水，按照交易时间降序取前10
     * @param code
     * @return
     */
    List<Map<String, Object>> getByCodeInfo(String code);

}
