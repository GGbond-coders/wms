package com.wms.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsDTO {
    private Long id;
    private String name;
    private String category;
    private String brand;
    private BigDecimal price;
    private String unit;
}