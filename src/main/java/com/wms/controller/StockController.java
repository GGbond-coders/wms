package com.wms.controller;

import com.wms.pojo.Stock;
import com.wms.service.StockService;
import com.wms.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping
    public ResultVO<List<Stock>> getStockList(
            @RequestParam(required = false) String goodsName,
            @RequestParam(required = false) Boolean lowStock) {
        List<Stock> stocks = stockService.getStockList(goodsName, lowStock);
        return ResultVO.success(stocks);
    }

    // Business rule: stock quantity must only change via inbound/outbound
    // Keep endpoint intentionally disabled to avoid direct modification.
    // (Inbound/Outbound will call StockService.updateStock internally.)
    // Previously exposed POST /stock/update has been removed.

    @PutMapping("/safe")
    public ResultVO<Void> setSafeStock(@RequestParam Long goodsId, @RequestParam Integer safeStock) {
        boolean result = stockService.setSafeStock(goodsId, safeStock);
        return result ? ResultVO.success() : ResultVO.error("设置安全库存失败");
    }
}

