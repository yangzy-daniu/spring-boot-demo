package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operation_log")
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module", nullable = false, length = 100)
    private String module; // 模块名称：用户管理、订单管理、系统管理

    @Column(name = "type", nullable = false, length = 50)
    private String type; // 操作类型：CREATE, UPDATE, DELETE, QUERY, LOGIN, etc.

    @Column(name = "operation", nullable = false, length = 200)
    private String operation; // 操作描述

    @Column(name = "operator", nullable = false, length = 100)
    private String operator; // 操作者姓名

    @Column(name = "operator_id")
    private Long operatorId; // 操作者ID

    @Column(name = "operator_ip", length = 64)
    private String operatorIp; // 操作者IP

    @Column(name = "request_url", length = 500)
    private String requestUrl; // 请求路径

    @Column(name = "request_method", length = 10)
    private String requestMethod; // 请求方式：GET, POST, PUT, DELETE

    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams; // 请求参数

    @Column(name = "result", length = 20)
    private String result; // 操作结果：SUCCESS, FAILURE

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage; // 错误信息

    @Column(name = "execution_time")
    private Long executionTime; // 执行时间(ms)

    @Column(name = "create_time")
    private LocalDateTime createTime; // 创建时间

    @Column(name = "tenant_id")
    private Long tenantId; // 租户ID

    @Column(name = "status_code")
    private Integer statusCode; // HTTP状态码

    @Column(name = "user_agent", length = 500)
    private String userAgent; // 用户代理

    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData; // 响应数据

    @Column(name = "access_time")
    private LocalDateTime accessTime; // 访问时间
}