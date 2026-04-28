package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.dto.GoodsDTO;
import com.wms.mapper.GoodsMapper;
import com.wms.pojo.Goods;
import com.wms.service.GoodsService;
import com.wms.vo.PageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public PageVO<Goods> getGoodsList(String name, String category, Integer page, Integer pageSize) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq("category", category);
        }
        Page<Goods> pageResult = goodsMapper.selectPage(new Page<>(page, pageSize), wrapper);
        PageVO<Goods> pageVO = new PageVO<>();
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setRows(pageResult.getRecords());
        return pageVO;
    }

    @Override
    public Goods getGoodsById(Long id) {
        return goodsMapper.selectById(id);
    }

    @Override
    public boolean addGoods(GoodsDTO goodsDTO) {
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsDTO, goods);
        return goodsMapper.insert(goods) > 0;
    }

    @Override
    public boolean updateGoods(GoodsDTO goodsDTO) {
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsDTO, goods);
        return goodsMapper.updateById(goods) > 0;
    }

    @Override
    public boolean deleteGoods(Long id) {
        return goodsMapper.deleteById(id) > 0;
    }
}