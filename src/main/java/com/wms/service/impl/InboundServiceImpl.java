package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.mapper.GoodsMapper;
import com.wms.mapper.InboundMapper;
import com.wms.mapper.UserMapper;
import com.wms.pojo.Goods;
import com.wms.pojo.Inbound;
import com.wms.pojo.User;
import com.wms.service.AuditLogService;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public PageVO<Inbound> getInboundList(String goodsName, Integer page, Integer pageSize) {
        QueryWrapper<Inbound> wrapper = new QueryWrapper<>();
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

        Page<Inbound> pageResult = inboundMapper.selectPage(new Page<>(page, pageSize), wrapper);
        PageVO<Inbound> pageVO = new PageVO<>();
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setRows(pageResult.getRecords());
        return pageVO;
    }

    @Override
    @Transactional
    public boolean addInbound(Long goodsId, Integer quantity, Long operatorId) {
        User operator = userMapper.selectById(operatorId);
        if (operator == null) {
            return false;
        }
        Inbound inbound = new Inbound();
        inbound.setGoodsId(goodsId);
        inbound.setQuantity(quantity);
        inbound.setOperatorId(operatorId);
        int result = inboundMapper.insert(inbound);

        boolean stockResult = stockService.updateStock(goodsId, quantity);
        boolean ok = result > 0 && stockResult;
        if (ok) {
            Goods goods = goodsMapper.selectById(goodsId);
            String name = goods != null ? goods.getName() : "";
            auditLogService.log("INBOUND_CREATE",
                    "inbound goodsId=" + goodsId + ", name=" + name + ", qty=" + quantity + ", operatorId=" + operatorId);
        }
        return ok;
    }
}
