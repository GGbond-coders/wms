package com.wms.controller;

import com.wms.dto.GoodsDTO;
import com.wms.pojo.Goods;
import com.wms.service.GoodsService;
import com.wms.vo.PageVO;
import com.wms.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        return result ? ResultVO.success() : ResultVO.error("新增商品失败");
    }

    @PutMapping
    public ResultVO<Void> updateGoods(@RequestBody GoodsDTO goodsDTO) {
        boolean result = goodsService.updateGoods(goodsDTO);
        return result ? ResultVO.success() : ResultVO.error("修改失败：库存不为0或数据不存在");
    }

    @DeleteMapping("/{id}")
    public ResultVO<Void> deleteGoods(@PathVariable Long id) {
        boolean result = goodsService.deleteGoods(id);
        return result ? ResultVO.success() : ResultVO.error("删除失败：库存不为0或数据不存在");
    }
}

