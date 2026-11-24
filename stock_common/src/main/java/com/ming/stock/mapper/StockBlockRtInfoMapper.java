package com.ming.stock.mapper;

import com.ming.stock.pojo.domain.StockBlockDomain;
import com.ming.stock.pojo.entity.StockBlockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author 11519
* @description 针对表【stock_block_rt_info(股票板块详情信息表)】的数据库操作Mapper
* @createDate 2026-08-02 09:34:48
* @Entity com.ming.stock.pojo.entity.StockBlockRtInfo
*/
public interface StockBlockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockBlockRtInfo record);

    int insertSelective(StockBlockRtInfo record);

    StockBlockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockBlockRtInfo record);

    int updateByPrimaryKey(StockBlockRtInfo record);

    /**
     * 获取国内板块数据 交易金额降序查询
     * @param lastDate 指定时间点
     * @return
     */
    List<StockBlockDomain> sectorAllLimit(@Param("lastDate") Date lastDate);

    /**
     * 板块信息批量插入
     * @param list
     */
    void insertBatch(@Param("list") List<StockBlockRtInfo> list);
}
