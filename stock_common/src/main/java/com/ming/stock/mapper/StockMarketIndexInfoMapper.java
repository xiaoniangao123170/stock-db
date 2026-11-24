package com.ming.stock.mapper;

import com.ming.stock.pojo.domain.InnerMarketDomain;
import com.ming.stock.pojo.entity.StockMarketIndexInfo;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author 11519
* @description 针对表【stock_market_index_info(国内大盘数据详情表)】的数据库操作Mapper
* @createDate 2026-08-02 09:34:48
* @Entity com.ming.stock.pojo.entity.StockMarketIndexInfo
*/
public interface StockMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockMarketIndexInfo record);

    int insertSelective(StockMarketIndexInfo record);

    StockMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockMarketIndexInfo record);

    int updateByPrimaryKey(StockMarketIndexInfo record);

    /**
     * 根据指定时间点查询指定大盘编码对应的数据
     * @param curDate  指定的时间点
     * @param mCodes    大盘编码集合
     * @return
     */
    List<InnerMarketDomain> getMarketInfo(@Param("curDate") Date curDate, @Param("mCodes") List<String> mCodes);

    /**
     * 统计国内A股大盘T日和T-1日的成交量对比功能(成交量为沪和深成交量之和)
     * @param tStartDate 起始时间
     * @param tEndDate   截至时间
     * @param inner      大盘集合编码
     * @return
     */
    List<Map> getComparedStockTradeAmt(@Param("tStartDate") Date tStartDate,
                                       @Param("tEndDate") Date tEndDate,
                                       @Param("inner") List<String> inner);

    /**
     * 批量插入大盘数据
     * @param entities 大盘实体对象的集合
     * @return
     */
    int insertBach(@Param("entities") ArrayList<StockMarketIndexInfo> entities);
}
