package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long id;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String description;
    private BigDecimal total;
}