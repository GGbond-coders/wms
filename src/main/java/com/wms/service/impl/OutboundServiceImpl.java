package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.mapper.GoodsMapper;
import com.wms.mapper.OutboundMapper;
import com.wms.mapper.StockMapper;
import com.wms.mapper.UserMapper;
import com.wms.pojo.Goods;
import com.wms.pojo.Outbound;
import com.wms.pojo.Stock;
import com.wms.pojo.User;
import com.wms.service.AuditLogService;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public PageVO<Outbound> getOutboundList(String goodsName, Integer page, Integer pageSize) {
        QueryWrapper<Outbound> wrapper = new QueryWrapper<>();
        if (goodsName != null && !goodsName.isEmpty()) {
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
    public boolean addOutbound(Long goodsId, Integer quantity, Long operatorId, String receiver) {
        User operator = userMapper.selectById(operatorId);
        if (operator == null) {
            return false;
        }
        QueryWrapper<Stock> stockWrapper = new QueryWrapper<>();
        stockWrapper.eq("goods_id", goodsId);
        Stock stock = stockMapper.selectOne(stockWrapper);
        if (stock == null || stock.getQuantity() < quantity) {
            return false;
        }

        Outbound outbound = new Outbound();
        outbound.setGoodsId(goodsId);
        outbound.setQuantity(quantity);
        outbound.setOperatorId(operatorId);
        outbound.setReceiver(receiver);
        int result = outboundMapper.insert(outbound);

        boolean stockResult = stockService.updateStock(goodsId, -quantity);
        boolean ok = result > 0 && stockResult;
        if (ok) {
            Goods goods = goodsMapper.selectById(goodsId);
            String name = goods != null ? goods.getName() : "";
            auditLogService.log("OUTBOUND_CREATE",
                    "outbound goodsId=" + goodsId + ", name=" + name + ", qty=" + quantity + ", operatorId=" + operatorId + ", receiver=" + receiver);
        }
        return ok;
    }
}
