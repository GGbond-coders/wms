package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.mapper.GoodsMapper;
import com.wms.mapper.OutboundMapper;
import com.wms.mapper.StockMapper;
import com.wms.pojo.Goods;
import com.wms.pojo.Outbound;
import com.wms.pojo.Stock;
import com.wms.service.OutboundService;
import com.wms.service.StockService;
import com.wms.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OutboundServiceImpl implements OutboundService {

    @Autowired
    private OutboundMapper outboundMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockService stockService;

    @Override
    public PageVO<Outbound> getOutboundList(String goodsName, Integer page, Integer pageSize) {
        QueryWrapper<Outbound> wrapper = new QueryWrapper<>();
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
        wrapper.orderByDesc("create_time");
        Page<Outbound> pageResult = outboundMapper.selectPage(new Page<>(page, pageSize), wrapper);
        PageVO<Outbound> pageVO = new PageVO<>();
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setRows(pageResult.getRecords());
        return pageVO;
    }

    @Override
    @Transactional
    public boolean addOutbound(Long goodsId, Integer quantity, String receiver) {
        // 检查库存是否足够
        QueryWrapper<Stock> stockWrapper = new QueryWrapper<>();
        stockWrapper.eq("goods_id", goodsId);
        Stock stock = stockMapper.selectOne(stockWrapper);
        if (stock == null || stock.getQuantity() < quantity) {
            return false; // 库存不足
        }

        // 创建出库记录
        Outbound outbound = new Outbound();
        outbound.setGoodsId(goodsId);
        outbound.setQuantity(quantity);
        outbound.setReceiver(receiver);
        int result = outboundMapper.insert(outbound);

        // 更新库存（减少）
        boolean stockResult = stockService.updateStock(goodsId, -quantity);

        // 记录操作日志
        // Goods goods = goodsMapper.selectById(goodsId);
        // String content = "出库商品: " + (goods != null ? goods.getName() : "未知") + ", 数量: " + quantity + ", 接收方: " + receiver;
        // operationLogService.addLog(receiver, "出库", content);

        return result > 0 && stockResult;
    }
}