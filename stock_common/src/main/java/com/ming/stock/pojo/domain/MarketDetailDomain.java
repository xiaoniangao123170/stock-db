package com.ming.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: Ming
 * @Description 个股最新分时行情数据
 */
@Schema(description = "个股最新分时行情数据")
@Data
public class MarketDetailDomain {
    @Schema(description = "最新交易量")
    private BigDecimal tradeAmt;//最新交易量
    @Schema(description = "前收盘价格")
    private BigDecimal preClosePrice;//前收盘价格
    @Schema(description = "最低价")
    private BigDecimal lowPrice;//最低价
    @Schema(description = "最高价")
    private BigDecimal highPrice;//最高价
    @Schema(description = "开盘价")
    private BigDecimal openPrice;//开盘价
    @Schema(description = "交易金额")
    private BigDecimal tradeVol;//交易金额
    @Schema(description = "当前价格")
    private BigDecimal tradePrice;//当前价格
    @Schema(description = "当前日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date curDate;//当前日期
}