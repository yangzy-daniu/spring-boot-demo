package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderLogDTO {
    private Long id;
    private String action;
    private String operator;
    private String type;
    private String remark;
    private LocalDateTime createTime;
}