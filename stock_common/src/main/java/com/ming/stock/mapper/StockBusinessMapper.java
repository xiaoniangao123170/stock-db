package com.ming.stock.mapper;

import com.ming.stock.pojo.domain.CodeSearchDomain;
import com.ming.stock.pojo.domain.StockDescribeDomain;
import com.ming.stock.pojo.entity.StockBusiness;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 11519
* @description 针对表【stock_business(主营业务表)】的数据库操作Mapper
* @createDate 2026-08-02 09:34:48
* @Entity com.ming.stock.pojo.entity.StockBusiness
*/
public interface StockBusinessMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockBusiness record);

    int insertSelective(StockBusiness record);

    StockBusiness selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockBusiness record);

    int updateByPrimaryKey(StockBusiness record);

    /**
     * 获取所有的A股编码集合
     * @return
     */
    List<String> getStockIds();

    /**
     * 根据股票编码模糊查询
     * @param searchStr
     * @return
     */
    List<CodeSearchDomain> getMsg(@Param("searchStr") String searchStr);

    StockDescribeDomain getStockDescribe(@Param("code") String code);
}
