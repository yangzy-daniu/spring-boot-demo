package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class NotificationVO {
    private Long id;
    private String title;
    private String content;
    private String description;
    private String type;
    private String icon;
    private String status;
    private Long senderId;
    private Long receiverId;
    private String receiverType;
    private Map<String, Object> extraData;
    private Integer priority;
    private Boolean important;
    private LocalDateTime expireTime;
    private LocalDateTime readTime;
    private LocalDateTime createTime;
    private String timeAgo; // 多久前
}