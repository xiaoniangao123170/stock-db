package com.ming.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: Ming
 * @Description 定义外盘的领域对象
 */

@Schema(description = "定义外盘的领域对象")
@Data
public class OutMarketDomain {
    @Schema(description = "大盘名称")
    private String name; //大盘名称
    @Schema(description = "当前大盘点")
    private BigDecimal curPoint;//当前大盘点
    @Schema(description = "涨跌值")
    private BigDecimal upDown;//涨跌值
    @Schema(description = "涨幅")
    private BigDecimal rose;  //涨幅
    @Schema(description = "当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date curTime;//当前时间


}
