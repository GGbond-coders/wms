package com.wms.controller;

import com.wms.service.StatisticsService;
import com.wms.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/stock")
    public ResultVO<Map<String, Object>> getStockStatistics() {
        Map<String, Object> data = statisticsService.getStockStatistics();
        return ResultVO.success(data);
    }

    @GetMapping("/io")
    public ResultVO<Map<String, Object>> getIOStatistics() {
        Map<String, Object> data = statisticsService.getIOStatistics();
        return ResultVO.success(data);
    }
}