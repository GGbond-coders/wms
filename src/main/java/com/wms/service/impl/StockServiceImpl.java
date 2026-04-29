package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wms.mapper.GoodsMapper;
import com.wms.mapper.StockMapper;
import com.wms.pojo.Goods;
import com.wms.pojo.Stock;
import com.wms.service.AuditLogService;
import com.wms.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public List<Stock> getStockList(String goodsName, Boolean lowStock) {
        QueryWrapper<Stock> wrapper = new QueryWrapper<>();
        if (goodsName != null && !goodsName.isEmpty()) {
            // 通过商品名称找到商品ID
            QueryWrapper<Goods> goodsWrapper = new QueryWrapper<>();
            goodsWrapper.like("name", goodsName);
            List<Goods> goodsList = goodsMapper.selectList(goodsWrapper);
            List<Long> goodsIds = goodsList.stream().map(Goods::getId).toList();
            if (!goodsIds.isEmpty()) {
                wrapper.in("goods_id", goodsIds);
            }
        }
        List<Stock> stocks = stockMapper.selectList(wrapper);

        if (lowStock != null && lowStock) {
            stocks = stocks.stream()
                    .filter(stock -> stock.getQuantity() < stock.getSafeStock())
                    .toList();
        }

        return stocks;
    }

    @Override
    public boolean updateStock(Long goodsId, Integer changeNum) {
        // 先查询当前库存
        QueryWrapper<Stock> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsId);
        Stock stock = stockMapper.selectOne(wrapper);
        if (stock == null) {
            // 如果没有库存记录，创建新的
            stock = new Stock();
            stock.setGoodsId(goodsId);
            stock.setQuantity(changeNum);
            stock.setSafeStock(10); // 默认安全库存
            return stockMapper.insert(stock) > 0;
        } else {
            // 更新库存
            stock.setQuantity(stock.getQuantity() + changeNum);
            return stockMapper.updateById(stock) > 0;
        }
    }

    @Override
    public boolean setSafeStock(Long goodsId, Integer safeStock) {
        UpdateWrapper<Stock> wrapper = new UpdateWrapper<>();
        wrapper.eq("goods_id", goodsId);
        wrapper.set("safe_stock", safeStock);
        boolean ok = stockMapper.update(null, wrapper) > 0;
        if (ok) {
            Goods goods = goodsMapper.selectById(goodsId);
            String name = goods != null ? goods.getName() : "";
            auditLogService.log("STOCK_SAFE_SET", "set safe stock goodsId=" + goodsId + ", name=" + name + ", safeStock=" + safeStock);
        }
        return ok;
    }
}
