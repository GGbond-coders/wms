package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wms.mapper.GoodsMapper;
import com.wms.mapper.StockMapper;
import com.wms.pojo.Goods;
import com.wms.pojo.Stock;
import com.wms.service.WarningService;
import com.wms.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WarningServiceImpl implements WarningService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public PageVO<Map<String, Object>> getLowStockWarnings(Integer page, Integer pageSize) {
        // 查询所有库存低于安全库存的商品
        QueryWrapper<Stock> wrapper = new QueryWrapper<>();
        wrapper.apply("quantity < safe_stock");
        List<Stock> lowStocks = stockMapper.selectList(wrapper);

        // 获取商品信息
        List<Long> goodsIds = lowStocks.stream().map(Stock::getGoodsId).collect(Collectors.toList());
        if (goodsIds.isEmpty()) {
            PageVO<Map<String, Object>> pageVO = new PageVO<>();
            pageVO.setTotal(0L);
            pageVO.setRows(List.of());
            return pageVO;
        }

        Map<Long, Goods> goodsMap = goodsMapper.selectBatchIds(goodsIds)
                .stream()
                .collect(Collectors.toMap(Goods::getId, goods -> goods));

        // 组合数据
        List<Map<String, Object>> warnings = lowStocks.stream()
                .map(stock -> {
                    Map<String, Object> warning = new HashMap<>();
                    Goods goods = goodsMap.get(stock.getGoodsId());
                    if (goods != null) {
                        warning.put("goodsId", goods.getId());
                        warning.put("goodsName", goods.getName());
                        warning.put("category", goods.getCategory());
                        warning.put("currentStock", stock.getQuantity());
                        warning.put("safeStock", stock.getSafeStock());
                    }
                    return warning;
                })
                .collect(Collectors.toList());

        // 分页
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, warnings.size());
        List<Map<String, Object>> pageData = warnings.subList(start, end);

        PageVO<Map<String, Object>> pageVO = new PageVO<>();
        pageVO.setTotal((long) warnings.size());
        pageVO.setRows(pageData);

        return pageVO;
    }
}