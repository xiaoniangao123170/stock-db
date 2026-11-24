package com.ming.stock.controller;

import com.ming.stock.pojo.domain.*;
import com.ming.stock.service.StockService;
import com.ming.stock.vo.resp.PageResult;
import com.ming.stock.vo.resp.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: Ming
 * @Description 定义股票相关接口控制器
 */
@Tag(name = "定义股票相关接口控制器", description = "定义股票相关接口控制器")
@RestController
@RequestMapping("/api/quot")
public class StockController {
    @Autowired
    private StockService stockService;

    /**
     * 获取国内大盘的最新数据
     *
     * @return
     */
    @Operation(summary = "获取国内大盘的最新数据", description = "获取国内大盘的最新数据")
    @GetMapping("/index/all")
    public R<List<InnerMarketDomain>> getInnerMarketInfo() {
        return stockService.getInnerMarketInfo();

    }

    /**
     * 获取国内板块数据 交易金额降序查询
     *
     * @return
     */
    @Operation(summary = "获取国内板块数据 交易金额降序查询", description = "获取国内板块数据 交易金额降序查询")
    @GetMapping("/sector/all")
    public R<List<StockBlockDomain>> sectorAll() {
        return stockService.sectorAllLimit();

    }

    /**
     * 分页查询股票最新数据 并且按照涨幅排序查询
     * @param page   当前页
     * @param pageSize 每页的大小
     * @return
     */
    @Parameters({
            @Parameter(name = "page", description = "当前页", in = ParameterIn.QUERY),
            @Parameter(name = "pageSize", description = "每页的大小", in = ParameterIn.QUERY)
    })
    @Operation(summary = "分页查询股票最新数据 并且按照涨幅排序查询", description = "分页查询股票最新数据 并且按照涨幅排序查询")
    @GetMapping("/stock/all")
    public R<PageResult<StockUpdownDomain>> getStockPageInfo(
            @RequestParam(name = "page",required = false,defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") Integer pageSize){
        return stockService.getStockPageInfo(page,pageSize);
    }
    /**
     * 统计沪深两市个股最新交易数据，并按涨幅降序排序查询前4条数据
     * @return
     */

    @Operation(summary = "统计沪深两市个股最新交易数据，并按涨幅降序排序查询前4条数据", description = "统计沪深两市个股最新交易数据，并按涨幅降序排序查询前4条数据")
    @GetMapping("/stock/increase")
    @ResponseBody
    public R<List<StockUpdownDomain>> getTopStocksByIncrease() {
        return stockService.getTopStocksByIncrease();
    }

    /**
     *
     * 统计最新股票交易日内每分钟的涨跌停的股票数量
     * @return
     */
    @Operation(summary = "统计最新股票交易日内每分钟的涨跌停的股票数量", description = "统计最新股票交易日内每分钟的涨跌停的股票数量")
    @GetMapping("/stock/updown/count")
    public R<Map<String,List>> getStockUpdownCount(){
        return stockService.getStockUpdownCount();
    }

    /**
     * 将指定也的股票数据导出到excel表中
     * @param page  当前页
     * @param pageSize 每页大小
     * @param response 拦截响应数据
     */
    @Parameters({
            @Parameter(name = "page", description = "当前页", in = ParameterIn.QUERY),
            @Parameter(name = "pageSize", description = "每页大小", in = ParameterIn.QUERY)
    })
    @Operation(summary = "将指定也的股票数据导出到excel表中", description = "将指定也的股票数据导出到excel表中")
    @GetMapping("/stock/export")
    public void exportStockUpdownInfo(
            @RequestParam(name = "page",required = false,defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") Integer pageSize,
            HttpServletResponse response){
        stockService.exportStockUpdownInfo(page,pageSize,response);
    }

    /**
     * 统计国内A股大盘T日和T-1日的成交量对比功能(成交量为沪和深成交量之和)
     * @return
     */
    @Operation(summary = "统计国内A股大盘T日和T-1日的成交量对比功能(成交量为沪和深成交量之和)", description = "统计国内A股大盘T日和T-1日的成交量对比功能(成交量为沪和深成交量之和)")
    @GetMapping("/stock/tradeAmt")
    public R<Map<String,List>> getComparedStockTradeAmt(){
        return stockService.getComparedStockTradeAmt();
    }

    /**
     *  统计最新交易时间殿下股票 在各个涨幅区间的数量
     * @return
     */
    @Operation(summary = "统计最新交易时间殿下股票 在各个涨幅区间的数量", description = "统计最新交易时间殿下股票 在各个涨幅区间的数量")
    @GetMapping("/stock/updown")
    public R<Map> getIncreaseRangeInfo(){
        return stockService.getIncreaseRangeInfo();
    }

    /**
     * 查询单个股票分时行情数据 如果日期不在有效时间内 则以最近一个股票交易时间作为查询时间点 获取指定股票T日分时数据
     * @param stockCode
     * @return
     */
    @GetMapping("/stock/screen/time-sharing")
    public R<List<Stock4MinuteDomain>> getStockScreenTimeSharing(
            @RequestParam(name = "code",required = true) String stockCode){
        return stockService.getStockScreenTimeSharing(stockCode);
    }

    /**
     * 单个个股日K数据查询 可以根据时间区间查询数日的K线数据
     * @param stockCode 股票编码
     * @return
     */
    @GetMapping("/stock/screen/dkline")
    public R<List<Stock4EvrDayDomain>> getStockScreenDkLine(
            @RequestParam(value = "code",required = true) String stockCode){
        return stockService.getStockScreenDkLine(stockCode);
    }

}
