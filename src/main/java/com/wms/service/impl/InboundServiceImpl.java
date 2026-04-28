package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.mapper.GoodsMapper;
import com.wms.mapper.InboundMapper;
import com.wms.pojo.Goods;
import com.wms.pojo.Inbound;
import com.wms.service.InboundService;
import com.wms.service.StockService;
import com.wms.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InboundServiceImpl implements InboundService {

    @Autowired
    private InboundMapper inboundMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private StockService stockService;

    @Override
    public PageVO<Inbound> getInboundList(String goodsName, Integer page, Integer pageSize) {
        QueryWrapper<Inbound> wrapper = new QueryWrapper<>();
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
        Page<Inbound> pageResult = inboundMapper.selectPage(new Page<>(page, pageSize), wrapper);
        PageVO<Inbound> pageVO = new PageVO<>();
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setRows(pageResult.getRecords());
        return pageVO;
    }

    @Override
    @Transactional
    public boolean addInbound(Long goodsId, Integer quantity, String operator) {
        // 创建入库记录
        Inbound inbound = new Inbound();
        inbound.setGoodsId(goodsId);
        inbound.setQuantity(quantity);
        inbound.setOperator(operator);
        int result = inboundMapper.insert(inbound);

        // 更新库存
        boolean stockResult = stockService.updateStock(goodsId, quantity);

        // 记录操作日志
        // Goods goods = goodsMapper.selectById(goodsId);
        // String content = "入库商品: " + (goods != null ? goods.getName() : "未知") + ", 数量: " + quantity;
        // operationLogService.addLog(operator, "入库", content);

        return result > 0 && stockResult;
    }
}