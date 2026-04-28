package com.wms.service;

import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getStockStatistics();

    Map<String, Object> getIOStatistics();
}