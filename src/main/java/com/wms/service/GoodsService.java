package com.wms.service;

import com.wms.dto.GoodsDTO;
import com.wms.pojo.Goods;
import com.wms.vo.PageVO;

public interface GoodsService {
    PageVO<Goods> getGoodsList(String name, String category, Integer page, Integer pageSize);

    Goods getGoodsById(Long id);

    boolean addGoods(GoodsDTO goodsDTO);

    boolean updateGoods(GoodsDTO goodsDTO);

    boolean deleteGoods(Long id);
}