package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
    private String customer;
    private String remark;
    private List<OrderItemRequest> items;
}