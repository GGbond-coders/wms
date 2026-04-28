package com.wms.service;

import com.wms.pojo.Stock;

import java.util.List;

public interface StockService {
    List<Stock> getStockList(String goodsName, Boolean lowStock);

    boolean updateStock(Long goodsId, Integer changeNum);

    boolean setSafeStock(Long goodsId, Integer safeStock);
}