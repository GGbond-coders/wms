package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wms.mapper.InboundMapper;
import com.wms.mapper.OutboundMapper;
import com.wms.mapper.StockMapper;
import com.wms.pojo.Inbound;
import com.wms.pojo.Outbound;
import com.wms.pojo.Stock;
import com.wms.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private InboundMapper inboundMapper;

    @Autowired
    private OutboundMapper outboundMapper;

    @Override
    public Map<String, Object> getStockStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 商品总数
        long totalGoods = stockMapper.selectCount(null);

        // 库存总量
        List<Stock> stocks = stockMapper.selectList(null);
        int totalStock = stocks.stream().mapToInt(Stock::getQuantity).sum();

        result.put("totalGoods", totalGoods);
        result.put("totalStock", totalStock);

        return result;
    }

    @Override
    public Map<String, Object> getIOStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 入库总量
        QueryWrapper<Inbound> inboundWrapper = new QueryWrapper<>();
        List<Inbound> inbounds = inboundMapper.selectList(inboundWrapper);
        int inboundTotal = inbounds.stream().mapToInt(Inbound::getQuantity).sum();

        // 出库总量
        QueryWrapper<Outbound> outboundWrapper = new QueryWrapper<>();
        List<Outbound> outbounds = outboundMapper.selectList(outboundWrapper);
        int outboundTotal = outbounds.stream().mapToInt(Outbound::getQuantity).sum();

        result.put("inboundTotal", inboundTotal);
        result.put("outboundTotal", outboundTotal);

        return result;
    }
}