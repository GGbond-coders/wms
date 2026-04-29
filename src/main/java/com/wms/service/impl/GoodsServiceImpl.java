package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.dto.GoodsDTO;
import com.wms.mapper.GoodsMapper;
import com.wms.mapper.StockMapper;
import com.wms.pojo.Goods;
import com.wms.pojo.Stock;
import com.wms.service.AuditLogService;
import com.wms.service.GoodsService;
import com.wms.vo.PageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AuditLogService auditLogService;

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
        boolean ok = goodsMapper.insert(goods) > 0;
        if (ok) {
            auditLogService.log("GOODS_CREATE", "create goods id=" + goods.getId() + ", name=" + goods.getName());
        }
        return ok;
    }

    @Override
    public boolean updateGoods(GoodsDTO goodsDTO) {
        // Business rule: if stock quantity != 0, disallow update
        if (goodsDTO.getId() != null) {
            Stock stock = stockMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Stock>()
                    .eq("goods_id", goodsDTO.getId()));
            if (stock != null && stock.getQuantity() != null && stock.getQuantity() != 0) {
                return false;
            }
        }
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsDTO, goods);
        boolean ok = goodsMapper.updateById(goods) > 0;
        if (ok) {
            auditLogService.log("GOODS_UPDATE", "update goods id=" + goods.getId() + ", name=" + goods.getName());
        }
        return ok;
    }

    @Override
    public boolean deleteGoods(Long id) {
        // Business rule: if stock quantity != 0, disallow delete
        Stock stock = stockMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Stock>()
                .eq("goods_id", id));
        if (stock != null && stock.getQuantity() != null && stock.getQuantity() != 0) {
            return false;
        }
        Goods goods = goodsMapper.selectById(id);
        boolean ok = goodsMapper.deleteById(id) > 0;
        if (ok) {
            auditLogService.log("GOODS_DELETE", "delete goods id=" + id + ", name=" + (goods != null ? goods.getName() : ""));
        }
        return ok;
    }
}
