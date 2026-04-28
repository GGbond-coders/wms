package com.wms.controller;

import com.wms.pojo.Stock;
import com.wms.service.StockService;
import com.wms.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/update")
    public ResultVO<Void> updateStock(@RequestParam Long goodsId, @RequestParam Integer changeNum) {
        boolean result = stockService.updateStock(goodsId, changeNum);
        return result ? ResultVO.success() : ResultVO.error("更新库存失败");
    }

    @PutMapping("/safe")
    public ResultVO<Void> setSafeStock(@RequestParam Long goodsId, @RequestParam Integer safeStock) {
        boolean result = stockService.setSafeStock(goodsId, safeStock);
        return result ? ResultVO.success() : ResultVO.error("设置安全库存失败");
    }
}