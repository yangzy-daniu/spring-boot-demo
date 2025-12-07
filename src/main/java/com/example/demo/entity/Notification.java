package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "system_notification")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String description;

    private String type; // SYSTEM, USER, ORDER, WARNING, SUCCESS, INFO

    private String icon; // 图标名称

    @Column(nullable = false)
    private String status; // UNREAD, READ, ARCHIVED

    @Column(name = "sender_id")
    private Long senderId = 0L; // 0表示系统发送

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId = 1L; // 默认发送给管理员

    @Column(name = "receiver_type")
    private String receiverType = "USER"; // USER, ROLE, ALL

    @Column(columnDefinition = "JSON")
    private String extraData; // 额外数据，JSON格式

    private Integer priority = 0; // 优先级，0-10，越高越重要

    private Boolean important = false; // 是否重要

    @Column(name = "expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Column(name = "read_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    @Column(name = "create_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Transient
    private Map<String, Object> extraDataMap;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) {
            status = "UNREAD";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}