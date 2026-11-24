package com.ming.stock.face.impl;

import com.ming.stock.constant.StockConstant;
import com.ming.stock.face.StockCacheFace;
import com.ming.stock.mapper.StockBusinessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@CacheConfig(cacheNames = StockConstant.STOCK)
public class StockCacheFaceImpl implements StockCacheFace {

    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    @Cacheable(key = "#root.method.getName()")
    @Override
    public List<String> getAllStockCodeWithPrefix() {
                List<String> stockIds = stockBusinessMapper.getStockIds();
        System.out.println(stockIds);
        //添加大盘业务前缀 sh sz
        stockIds = stockIds.stream().map(code -> code.startsWith("6") ? "sh" + code : "sz" + code).collect(Collectors.toList());
        System.out.println(stockIds);
        return stockIds;
    }
}

