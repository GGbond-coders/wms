package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wms.mapper.InboundMapper;
import com.wms.mapper.OutboundMapper;
import com.wms.mapper.StatisticsMapper;
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

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    public Map<String, Object> getStockStatistics() {
        Map<String, Object> result = new HashMap<>();

        long totalGoods = stockMapper.selectCount(null);
        List<Stock> stocks = stockMapper.selectList(null);
        int totalStock = stocks.stream().mapToInt(Stock::getQuantity).sum();

        result.put("totalGoods", totalGoods);
        result.put("totalStock", totalStock);
        return result;
    }

    @Override
    public Map<String, Object> getIOStatistics() {
        Map<String, Object> result = new HashMap<>();

        List<Inbound> inbounds = inboundMapper.selectList(new QueryWrapper<>());
        int inboundTotal = inbounds.stream().mapToInt(Inbound::getQuantity).sum();

        List<Outbound> outbounds = outboundMapper.selectList(new QueryWrapper<>());
        int outboundTotal = outbounds.stream().mapToInt(Outbound::getQuantity).sum();

        result.put("inboundTotal", inboundTotal);
        result.put("outboundTotal", outboundTotal);
        return result;
    }

    @Override
    public List<Map<String, Object>> getCategoryShare() {
        return statisticsMapper.stockByCategory();
    }

    @Override
    public Map<String, Object> getTrend30d() {
        Map<String, Object> result = new HashMap<>();
        result.put("inbound", statisticsMapper.inboundTrend30d());
        result.put("outbound", statisticsMapper.outboundTrend30d());
        return result;
    }
}
