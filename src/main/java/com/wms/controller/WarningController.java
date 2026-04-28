package com.wms.controller;

import com.wms.service.WarningService;
import com.wms.vo.PageVO;
import com.wms.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/warning")
public class WarningController {

    @Autowired
    private WarningService warningService;

    @GetMapping("/low-stock")
    public ResultVO<PageVO<Map<String, Object>>> getLowStockWarnings(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Map<String, Object>> pageVO = warningService.getLowStockWarnings(page, pageSize);
        return ResultVO.success(pageVO);
    }
}