package com.ming.stock.pojo.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: Ming
 * @Description 个股主营业务查询
 */

@Schema(description = "个股主营业务查询")
@Data
public class StockDescribeDomain {
    @Schema(description = "股票编码")
    private String code;//股票编码
    @Schema(description = "行业，也就是行业板块名称")
    private String trade;//行业，也就是行业板块名称
    @Schema(description = "公司主营业务")
    private String business;//公司主营业务
    @Schema(description = "公司名称")
    private String name;//公司名称
}
