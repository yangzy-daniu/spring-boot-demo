package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequest {
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String description;
}