package com.wms.service;

import com.wms.vo.PageVO;

import java.util.Map;

public interface WarningService {
    PageVO<Map<String, Object>> getLowStockWarnings(Integer page, Integer pageSize);
}