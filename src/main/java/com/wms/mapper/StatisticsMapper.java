package com.wms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticsMapper {

    @Select("""
            SELECT IFNULL(g.category, '') AS category, SUM(s.quantity) AS quantity
            FROM goods g
            JOIN stock s ON s.goods_id = g.id
            GROUP BY g.category
            ORDER BY quantity DESC
            """)
    List<Map<String, Object>> stockByCategory();

    @Select("""
            SELECT DATE(i.create_time) AS day, SUM(i.quantity) AS quantity
            FROM inbound i
            WHERE i.create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
            GROUP BY DATE(i.create_time)
            ORDER BY day ASC
            """)
    List<Map<String, Object>> inboundTrend30d();

    @Select("""
            SELECT DATE(o.create_time) AS day, SUM(o.quantity) AS quantity
            FROM outbound o
            WHERE o.create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
            GROUP BY DATE(o.create_time)
            ORDER BY day ASC
            """)
    List<Map<String, Object>> outboundTrend30d();
}

