package com.ming.stock.pojo.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: Ming
 * @Description 股票Code联想推荐
 */
@Schema(description = "股票Code联想推荐")
@Data
public class CodeSearchDomain {
    @Schema(description = "股票编码")
    private String code; //股票编码
    @Schema(description = "股票名称")
    private String name; //股票名称
}
