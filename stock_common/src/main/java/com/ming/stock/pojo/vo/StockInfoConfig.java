package com.ming.stock.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Author: Ming
 * @Description TODO
 */
@Schema(description = "TODO")
@ConfigurationProperties(prefix = "stock")
@Data
public class StockInfoConfig {
    //封装国内A股大盘编码集合
    @Schema(description = "封装国内A股大盘编码集合")
    private List<String> inner;
    //外盘编码集合
    @Schema(description = "外盘编码集合")
    private List<String> outer;
    //股票涨幅区间标题集合
    @Schema(description = "股票涨幅区间标题集合")
    private List<String> upDownRange;
    //大盘 外盘 个股的公共的url
    @Schema(description = "大盘 外盘 个股的公共的url")
    private String marketUrl;
    //板块采集的url
    @Schema(description = "板块采集的url")
    private String blockUrl;

}
