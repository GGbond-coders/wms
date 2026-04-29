package com.wms.service;

import java.util.Map;
import java.util.List;

public interface StatisticsService {
    Map<String, Object> getStockStatistics();

    Map<String, Object> getIOStatistics();

    List<Map<String, Object>> getCategoryShare();

    Map<String, Object> getTrend30d();
}
