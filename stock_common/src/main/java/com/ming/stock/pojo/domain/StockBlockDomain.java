package com.ming.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: Ming
 * @Description 股票板块domain
 */
@Schema(description = "股票板块domain")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockBlockDomain {
    //公司数量
    @Schema(description = "公司数量")
    private Integer companyNum;
    //交易量
    @Schema(description = "交易量")
    private Long tradeAmt;
    //板块编码
    @Schema(description = "板块编码")
    private String code;
    //平均价
    @Schema(description = "平均价")
    private BigDecimal avgPrice;
    //板块名称
    @Schema(description = "板块名称")
    private String name;
    /**
     * 当前日期
     * 序列化(后端->前端 出参)
     * Java的Date对象输出JSON的时候 不在输出时间戳/ISO 格式 2026-8-6 9:44:00
     * "2026-8-6 9:44"
     * 反序列化:前端json->java对象 入参
     * @RequestBody接收前端json字符串"2026-8-6 9:44" 字段转成java Date对象
     */
    @Schema(description = "当前日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date curDate;
    //交易金额
    @Schema(description = "交易金额")
    private BigDecimal tradeVol;
    //涨跌值
    @Schema(description = "涨跌值")
    private BigDecimal updownRate;
}
