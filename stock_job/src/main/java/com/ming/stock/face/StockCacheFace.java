package com.ming.stock.face;

import java.util.List;

/**
 * 定义股票缓存层
 */
public interface StockCacheFace {
    /**
     * 获取所有股票的编码 并且添加上证或者深证的股票前缀sh sz
     * @return
     */
    List<String> getAllStockCodeWithPrefix();
}
