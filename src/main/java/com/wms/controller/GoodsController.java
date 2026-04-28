package com.wms.controller;

import com.wms.dto.GoodsDTO;
import com.wms.pojo.Goods;
import com.wms.service.GoodsService;
import com.wms.vo.PageVO;
import com.wms.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping
    public ResultVO<PageVO<Goods>> getGoodsList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Goods> pageVO = goodsService.getGoodsList(name, category, page, pageSize);
        return ResultVO.success(pageVO);
    }

    @GetMapping("/{id}")
    public ResultVO<Goods> getGoodsById(@PathVariable Long id) {
        Goods goods = goodsService.getGoodsById(id);
        return ResultVO.success(goods);
    }

    @PostMapping
    public ResultVO<Void> addGoods(@RequestBody GoodsDTO goodsDTO) {
        boolean result = goodsService.addGoods(goodsDTO);
        return result ? ResultVO.success() : ResultVO.error("添加失败");
    }

    @PutMapping
    public ResultVO<Void> updateGoods(@RequestBody GoodsDTO goodsDTO) {
        boolean result = goodsService.updateGoods(goodsDTO);
        return result ? ResultVO.success() : ResultVO.error("修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultVO<Void> deleteGoods(@PathVariable Long id) {
        boolean result = goodsService.deleteGoods(id);
        return result ? ResultVO.success() : ResultVO.error("删除失败");
    }
}