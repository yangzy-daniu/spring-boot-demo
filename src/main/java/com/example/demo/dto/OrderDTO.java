package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String orderNo;
    private String customer;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createTime;
    private Integer productCount;
    private String remark;
    private List<OrderItemDTO> items;
    private List<OrderLogDTO> logs;
}
