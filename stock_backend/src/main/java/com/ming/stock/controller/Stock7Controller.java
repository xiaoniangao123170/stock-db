package com.ming.stock.controller;

import com.ming.stock.pojo.domain.*;
import com.ming.stock.service.Stock7Service;
import com.ming.stock.vo.resp.R;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: Ming
 * @Description 定义day07作业
 */
@RestController
@RequestMapping("/api/quot")
public class Stock7Controller {
    @Autowired
    private Stock7Service stockService;
    /**
     * 外盘数据展示
     * @return
     */
    @Operation(summary = "外盘数据展示", description = "外盘数据展示")
    @GetMapping("/external/index")
    public R<List<OutMarketDomain>> getOutMarket(){
        return stockService.getOutMarket();
    }
    /**
     * 股票Code联想推荐功能
     * @param searchStr
     * @return
     */
    @GetMapping("/stock/search")
    public R<List<CodeSearchDomain>> codeSearch(@RequestParam(name = "searchStr" , required = false ,defaultValue = "0") String searchStr){
        return stockService.stockCodeSearch(searchStr);
    }
    /**
     * 个股描述功能实现
     * @param code
     * @return
     */
    @GetMapping("stock/describe")
    public R<StockDescribeDomain> getStockDescribe(@RequestParam(name = "code" , required = true) String code){
        return stockService.getStockDescribe(code);
    }
    /**
     * 功能描述：统计每周内的股票数据信息，信息包含：
     * 股票ID、 一周内最高价、 一周内最低价 、周1开盘价、周5的收盘价、
     * 整周均价、以及一周内最大交易日期（一般是周五所对应日期）
     *
     * @param code
     * @return
     */
    @GetMapping("/stock/screen/weekkline")
    public R<List<WeekLineDomain>> getWeekkline(@RequestParam String code){
        return stockService.getWeekLine(code);
    }
    /**
     * 获取个股最新分时行情数据，主要包含：
     * 	开盘价、前收盘价、最新价、最高价、最低价、成交金额和成交量、交易时间信息
     * @param code
     * @return
     */
    @GetMapping("/stock/screen/second/detail")
    public R<MarketDetailDomain> getMarketDetail(@RequestParam(name = "code" , required = true) String code){
        return stockService.getMarketDetail(code);
    }
    /**
     * 个股交易流水行情数据查询--查询最新交易流水，按照交易时间降序取前10
     * @param code
     * @return
     */
    @GetMapping("/stock/screen/second")
    public R<List<Map<String, Object>>> getStockTradinStatement(@RequestParam("code") String code) {
        return stockService.getStockTradinStatement(code);
    }

}
