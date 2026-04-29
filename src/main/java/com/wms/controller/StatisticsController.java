package com.wms.controller;

import com.wms.service.StatisticsService;
import com.wms.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/stock")
    public ResultVO<Map<String, Object>> getStockStatistics() {
        return ResultVO.success(statisticsService.getStockStatistics());
    }

    @GetMapping("/io")
    public ResultVO<Map<String, Object>> getIOStatistics() {
        return ResultVO.success(statisticsService.getIOStatistics());
    }

    @GetMapping("/category-share")
    public ResultVO<List<Map<String, Object>>> getCategoryShare() {
        return ResultVO.success(statisticsService.getCategoryShare());
    }

    @GetMapping("/trend-30d")
    public ResultVO<Map<String, Object>> getTrend30d() {
        return ResultVO.success(statisticsService.getTrend30d());
    }
}

