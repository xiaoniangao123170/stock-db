package com.ming.stock.service.impl;

import com.alibaba.excel.EasyExcel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ming.stock.cache.InnerMarketCacheService;
import com.ming.stock.mapper.StockBlockRtInfoMapper;
import com.ming.stock.mapper.StockMarketIndexInfoMapper;
import com.ming.stock.mapper.StockRtInfoMapper;
import com.ming.stock.pojo.domain.*;
import com.ming.stock.pojo.vo.StockInfoConfig;
import com.ming.stock.service.StockService;
import com.ming.stock.utils.DateTimeUtil;
import com.ming.stock.vo.resp.PageResult;
import com.ming.stock.vo.resp.R;
import com.ming.stock.vo.resp.ResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Ming
 * @Description TODO
 */
@Service
@Slf4j
public class StockServiceImpl implements StockService {
    @Autowired
    private StockInfoConfig stockInfoConfig;
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;
    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;
    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;
    @Autowired
    private InnerMarketCacheService innerMarketCacheService;

    /**
     * 获取国内大盘最新数据
     *
     * @return
     */
    @Override
    public R<List<InnerMarketDomain>> getInnerMarketInfo() {
        // 两级缓存：Caffeine L1 -> Redis L2 -> MySQL
        List<InnerMarketDomain> data = innerMarketCacheService.get(this::loadInnerMarketInfo);
        return R.ok(data);
    }

    /**
     * 回源 MySQL，查询国内大盘最新快照。
     * 注意：这里的历史 mock 时间要等真实采集数据接入后删掉。
     */
    private List<InnerMarketDomain> loadInnerMarketInfo() {
        //1.获取股票最新的交易时间点(精确到分钟 秒和毫秒置为0)
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //因为最新数据还没有 所以需要mock data 等后续股票采集job工程有了之后 在删除就行了
        curDate = DateTime.parse("2024-12-28 09:31:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.获取大盘编码集合
        List<String> mCodes = stockInfoConfig.getInner();
        //3.调用mapper查询数据
        return stockMarketIndexInfoMapper.getMarketInfo(curDate, mCodes);
    }

    /**
     * 获取国内板块数据 交易金额降序查询
     *
     * @return
     */
    @Override
    public R<List<StockBlockDomain>> sectorAllLimit() {
        //获取股票最新交易时间点
        Date lastDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //mock数据,后续删除
        lastDate = DateTime.parse("2024-12-21 09:30:01", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.调用mapper接口获取数据
        List<StockBlockDomain> infos = stockBlockRtInfoMapper.sectorAllLimit(lastDate);
        //2.组装数据
        //判断数据是否为空
        if (CollectionUtils.isEmpty(infos)) {
            return R.error(ResponseCode.NO_RESPONSE_DATA.getMessage());
        }
        return R.ok(infos);
    }

    /**
     * 分页查询股票最新数据 并且按照涨幅排序查询
     *
     * @param page     当前页
     * @param pageSize 每页的大小
     * @return
     */
    @Override
    public R<PageResult<StockUpdownDomain>> getStockPageInfo(Integer page, Integer pageSize) {
        //1.获取股票最新交易时间点
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        curDate = DateTime.parse("2024-12-30 09:32:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.设置PageHelper分页参数
        PageHelper.startPage(page, pageSize);
        //3.调用mapper查询
        List<StockUpdownDomain> pageData = stockRtInfoMapper.getStockPageInfo(curDate);
        //4.组装PageInfo对象 获取分页的具体信息 PageINfo有很多分页信息 但是部分是前端不需要的
        PageInfo<StockUpdownDomain> pageInfo = new PageInfo<>(pageData);
        PageResult<StockUpdownDomain> pageResult = new PageResult<>(pageInfo);
        return R.ok(pageResult);
    }

    /**
     * 统计沪深两市个股最新交易数据，并按涨幅降序排序查询前4条数据
     *
     * @return
     */
    @Override
    public R<List<StockUpdownDomain>> getTopStocksByIncrease() {
        //1.获取股票最新的交易时间点（精确到分钟，秒和毫秒置为0）
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //因为是最新所有还没有数据 所以需要 mock data  等后续完成股票采集job工程 再将删除即可
        curDate = DateTime.parse("2024-12-30 09:42:00",
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        // 调用仓库方法获取涨幅最大的股票
        List<StockUpdownDomain> topStocks = stockRtInfoMapper.findTopStocksByIncrease(curDate);
        // 返回成功响应和股票列表
        return R.ok(topStocks);
    }

    /**
     * 统计最新股票交易日内每分钟的涨跌停的股票数量
     *
     * @return
     */
    @Override
    public R<Map<String, List>> getStockUpdownCount() {
        //1.获取最新股票交易时间点
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        curDateTime = DateTime.parse("2024-12-31 14:58:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date endDate = curDateTime.toDate();
        //2.获取对应的开盘时间
        Date startDate = DateTimeUtil.getOpenDate(curDateTime).toDate();
        //3.统计涨停数据  约定mapper中flag入参  1:涨停数据 0:跌停
        List<Map> upList = stockRtInfoMapper.getStockUpdownCount(startDate, endDate, 1);
        //4.统计跌停数据
        List<Map> downList = stockRtInfoMapper.getStockUpdownCount(startDate, endDate, 0);
        //5.组装数据
        HashMap<String, List> info = new HashMap<>();
        info.put("upList", upList);
        info.put("downList", downList);
        //6.返回结果
        return R.ok(info);
    }

    /**
     * 将指定也的股票数据导出到excel表中
     *
     * @param page     当前页
     * @param pageSize 每页大小
     * @param response 拦截响应数据
     */
    @Override
    public void exportStockUpdownInfo(Integer page, Integer pageSize, HttpServletResponse response) {
        //1.获取分页数据
        R<PageResult<StockUpdownDomain>> r = this.getStockPageInfo(page, pageSize);
        List<StockUpdownDomain> rows = r.getData().getRows();
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        try {
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("股票信息表", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), StockUpdownDomain.class)
                    .sheet("股票信息表").doWrite(rows);
        } catch (IOException e) {
            log.error("当前页码:{},每页的大小:{},当前时间:{},异常信息:{}",
                    page, pageSize, DateTime.now().toString("yyyy-MM-dd HH:mm:ss"), e.getMessage());
            //通知前端异常稍后重试
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            R<Object> error = R.error(ResponseCode.ERROR);
            try {
                String jsonData = new ObjectMapper().writeValueAsString(error);
                response.getWriter().write(jsonData);
            } catch (IOException ex) {
                log.error("当前页码:{},每页的大小:{},当前时间:{},异常信息:{}",
                        page, pageSize, DateTime.now().toString("yyyy-MM-dd HH:mm:ss"), ex.getMessage());
            }
        }
    }

    /**
     * 统计国内A股大盘T日和T-1日的成交量对比功能(成交量为沪和深成交量之和)
     *
     * @return
     */
    @Override
    public R<Map<String, List>> getComparedStockTradeAmt() {
        //1.获取T日和T-1日的开始时间和结束时间
        //1.1获取T日的时间
        DateTime tEndDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());//T日的截至时间点
        tEndDateTime = DateTime.parse("2024-12-28 14:59:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        //转化成Java中的Date jdbc默认识别
        Date tEndDate = tEndDateTime.toDate();
        //开盘时间
        Date tStartDate = DateTimeUtil.getOpenDate(tEndDateTime).toDate();
        //1.2获取T-1日的时间范围
        DateTime preTEndDateTime = DateTimeUtil.getPreviousTradingDay(tEndDateTime);
        preTEndDateTime = DateTime.parse("2024-12-27 14:59:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date preTEndDate = preTEndDateTime.toDate();
        //开盘时间
        Date tPreStartDate = DateTimeUtil.getOpenDate(preTEndDateTime).toDate();
        //2.调用mapper查询
        //2.1统计T日的交易数据  第三个参数是单盘编码集合
        List<Map> tData = stockMarketIndexInfoMapper.
                getComparedStockTradeAmt(tStartDate, tEndDate, stockInfoConfig.getInner());
        //2.2统计T-1日交易数据
        List<Map> preTData = stockMarketIndexInfoMapper
                .getComparedStockTradeAmt(tPreStartDate, preTEndDate, stockInfoConfig.getInner());
        //3.组装数据
        HashMap<String, List> info = new HashMap<>();
        info.put("amtList", tData);
        info.put("yesAmtList", preTData);
        //4.响应数据
        return R.ok(info);
    }

    /**
     * 统计最新交易时间殿下股票 在各个涨幅区间的数量
     *
     * @return
     */
    @Override
    public R<Map> getIncreaseRangeInfo() {
        //1.获取当前时间点
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        curDateTime = DateTime.parse("2024-12-19 09:47:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date curDate = curDateTime.toDate();
        //2.调用mapper数据
        List<Map> infos = stockRtInfoMapper.getIncreaseRangeInfo(curDate);
        //获取有序的涨幅区间标题集合
        List<String> upDownRange = stockInfoConfig.getUpDownRange();
        //将顺序的涨幅区内的每隔元素转化为map对象
        //方式1:
        /*List<Map> allInfos = new ArrayList<>();
        for (String title:upDownRange){
            Map tmp = null;
            for (Map info:infos){
                if (info.containsValue(title)){
                    tmp = info;
                    break;
                }
            }
            if (tmp==null){
                //不存在 补齐
                tmp = new HashMap();
                tmp.put("count",0);
                tmp.put("title",title);
            }
            allInfos.add(tmp);
        }*/
        //方式2:stream流遍历
        List<Map> allInfos = upDownRange.stream().map(title -> {
            Optional<Map> result = infos.stream().filter(map -> map.containsValue(title)).findFirst();
            //判断是否存在符合过滤的元素
            if (result.isPresent()){
                return result.get();
            }else {
                HashMap<String,Object> tmp = new HashMap<>();
                tmp.put("count",0);
                tmp.put("title",title);
                return tmp;
            }
        }).collect(Collectors.toList());
        //组装数据
        HashMap<String,Object> data = new HashMap<>();
        //获取指定日期格式的字符串
        data.put("time",curDateTime.toString("yyyy-MM-dd HH:mm:ss"));
        data.put("infos",allInfos);
        //返回数据
        return R.ok(data);
    }
    /**
     * 查询单个股票分时行情数据 如果日期不在有效时间内 则以最近一个股票交易时间作为查询时间点 获取指定股票T日分时数据
     * @param stockCode
     * @return
     */
    @Override
    public R<List<Stock4MinuteDomain>> getStockScreenTimeSharing(String stockCode) {
        //1.获取T日时间
        DateTime endDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        endDateTime=DateTime.parse("2024-12-30 14:30:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date endDate = endDateTime.toDate();
        Date openDate =DateTimeUtil.getOpenDate(endDateTime).toDate();
        //查询mapper
        List<Stock4MinuteDomain> data = stockRtInfoMapper.getStockScreenTimeSharing(openDate,endDate,stockCode);
        //返回数据
        return R.ok(data);
    }
    /**
     * 单个个股日K数据查询 可以根据时间区间查询数日的K线数据
     * @param stockCode 股票编码
     * @return
     */
   /* @Override
    public R<List<Stock4EvrDayDomain>> getStockScreenDkLine(String stockCode) {
        //1.获取日k数据的时间范围
        //获取截止时间
        DateTime endDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        endDateTime = DateTime.parse("2022-12-30 14:30:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date endDate = endDateTime.toDate();
        //获取开始时间
        DateTime startDateTime = endDateTime.minusDays(10);
        startDateTime = DateTime.parse("2022-1-30 14:30:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date startDate = startDateTime.toDate();
        //调用mapper获取指定日期范围内的日K线数据
        List<Stock4EvrDayDomain> dkLineData = stockRtInfoMapper.getStockScreenDkLine(startDate,endDate,stockCode);
        //返回数据
        return R.ok(dkLineData);
    }*/
    /**
     * 单个个股日K数据查询 可以根据时间区间查询数日的K线数据
     * @param stockCode 股票编码
     * @return
     */
    @Override
    public R<List<Stock4EvrDayDomain>> getStockScreenDkLine(String stockCode) {
        //1.获取日k数据的时间范围
        //获取截止时间
        DateTime endDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        endDateTime = DateTime.parse("2024-12-30 14:30:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date endDate = endDateTime.toDate();
        //获取开始时间
        DateTime startDateTime = endDateTime.minusDays(10);
        startDateTime = DateTime.parse("2024-1-30 14:30:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date startDate = startDateTime.toDate();
        //调用mapper获取指定日期范围内的日K线数据
        //分布实现
        List<Date> mxTimes = stockRtInfoMapper.getStockScreenDkLine2(startDate,endDate,stockCode);
        List<Stock4EvrDayDomain> infos = stockRtInfoMapper.getStockScreenDkLine3(mxTimes,stockCode);
        if (CollectionUtils.isEmpty(infos)){
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        //返回数据
        return R.ok(infos);
    }
}
