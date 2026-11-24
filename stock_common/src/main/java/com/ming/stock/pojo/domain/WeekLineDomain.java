package com.ming.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: Ming
 * @Description 个股周K线功能
 */

@Schema(description = "个股周K线功能")
@Data
public class WeekLineDomain {
    
    @Schema(description = "一周内平均价")
    private BigDecimal avgPrice;//一周内平均价
  
    @Schema(description = "一周内最低价")
    private BigDecimal minPrice;//一周内最低价
   
    @Schema(description = "周一开盘价")
    private BigDecimal openPrice;//周一开盘价
    
    @Schema(description = "一周内最高价")
    private BigDecimal maxPrice;//一周内最高价
  
    @Schema(description = "周五收盘价（如果当前日期不到周五，则显示最新价格）")
    private BigDecimal closePrice;//周五收盘价（如果当前日期不到周五，则显示最新价格）
    
    @Schema(description = "一周内最大时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date mxTime;//一周内最大时间
    
    @Schema(description = "股票编码")
    private String stockCode;//股票编码

    @Schema(description = "周标识（YEARWEEK值，如202327）")
    private Integer week;//[新增] 周标识，用于拆分查询后按周合并
}
