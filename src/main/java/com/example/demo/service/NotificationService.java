package com.example.demo.service;

import com.example.demo.entity.Notification;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.dto.NotificationVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Resource
    private NotificationRepository notificationRepository;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 分页查询通知
     */
    public Page<NotificationVO> getNotifications(Integer pageNum, Integer pageSize, String status, String type, Boolean important) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<Notification> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 默认查询当前用户的通知
            Long currentUserId = getCurrentUserId();
            predicates.add(cb.equal(root.get("receiverId"), currentUserId));

            if (StringUtils.hasText(status) && !"ALL".equals(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (StringUtils.hasText(type)) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (important != null) {
                predicates.add(cb.equal(root.get("important"), important));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Notification> notificationPage = notificationRepository.findAll(spec, pageable);

        // 转换为VO
        List<NotificationVO> voList = notificationPage.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageImpl<>(voList, pageable, notificationPage.getTotalElements());
    }

    /**
     * 获取通知详情
     */
    public NotificationVO getNotificationDetail(Long id) {
        Long currentUserId = getCurrentUserId();

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("通知不存在"));

        // 权限校验：只能查看自己的通知
        if (!notification.getReceiverId().equals(currentUserId)) {
            throw new RuntimeException("无权查看此通知");
        }

        return convertToVO(notification);
    }

    /**
     * 创建通知
     */
    @Transactional
    public Notification createNotification(Notification notification) {
        if (notification.getReceiverId() == null) {
            notification.setReceiverId(getCurrentUserId());
        }

        if (notification.getStatus() == null) {
            notification.setStatus("UNREAD");
        }

        if (notification.getExtraData() != null) {
            try {
                String extraDataJson = objectMapper.writeValueAsString(notification.getExtraDataMap());
                notification.setExtraData(extraDataJson);
            } catch (JsonProcessingException e) {
                log.error("转换extraData失败", e);
            }
        }

        return notificationRepository.save(notification);
    }

    /**
     * 创建系统通知
     */
    public void createSystemNotification(String title, String content, String type, Map<String, Object> extraData) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setDescription(content.length() > 50 ? content.substring(0, 50) + "..." : content);
        notification.setType(type);
        notification.setIcon(getIconByType(type));
        notification.setSenderId(0L);
        notification.setStatus("UNREAD");
        notification.setPriority(getPriorityByType(type));
        notification.setImportant(isImportantType(type));

        if (extraData != null) {
            notification.setExtraDataMap(extraData);
        }

        createNotification(notification);
    }

    /**
     * 标记为已读
     */
    @Transactional
    public void markAsRead(Long id) {
        Long currentUserId = getCurrentUserId();
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("通知不存在"));

        if (!notification.getReceiverId().equals(currentUserId)) {
            throw new RuntimeException("无权操作此通知");
        }

        if ("UNREAD".equals(notification.getStatus())) {
            notification.setStatus("READ");
            notification.setReadTime(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    /**
     * 批量标记为已读
     */
    @Transactional
    public void batchMarkAsRead(List<Long> ids) {
        Long currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        int updated = notificationRepository.batchMarkAsRead(ids, currentUserId, now, now);
        log.info("批量标记已读完成，更新了 {} 条通知", updated);
    }

    /**
     * 标记所有为已读
     */
    @Transactional
    public void markAllAsRead() {
        Long currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        int updated = notificationRepository.markAllAsRead(currentUserId, now, now);
        log.info("标记所有为已读，更新了 {} 条通知", updated);
    }

    /**
     * 获取未读数量
     */
    public Long getUnreadCount() {
        Long currentUserId = getCurrentUserId();
        return notificationRepository.countUnreadByReceiverId(currentUserId);
    }

    /**
     * 删除通知
     */
    @Transactional
    public void deleteNotification(Long id) {
        Long currentUserId = getCurrentUserId();
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("通知不存在"));

        if (!notification.getReceiverId().equals(currentUserId)) {
            throw new RuntimeException("无权删除此通知");
        }

        notificationRepository.deleteById(id);
    }

    /**
     * 批量删除通知
     */
    @Transactional
    public void batchDeleteNotifications(List<Long> ids) {
        Long currentUserId = getCurrentUserId();

        // 先验证权限
        List<Notification> notifications = notificationRepository.findAllById(ids);
        for (Notification notification : notifications) {
            if (!notification.getReceiverId().equals(currentUserId)) {
                throw new RuntimeException("无权删除通知ID: " + notification.getId());
            }
        }

        notificationRepository.deleteAllById(ids);
    }

    /**
     * 归档过期的通知（定时任务）
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    @Transactional
    public void archiveExpiredNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime updateTime = LocalDateTime.now();

        int archivedCount = notificationRepository.archiveExpiredNotifications(now, updateTime);
        if (archivedCount > 0) {
            log.info("已归档 {} 条过期的通知", archivedCount);
        }
    }

    /**
     * 获取工作台通知（最近5条）
     */
    public List<NotificationVO> getDashboardNotifications() {
        Long currentUserId = getCurrentUserId();
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<Notification> notificationPage = notificationRepository.findByReceiverIdOrderByCreateTimeDesc(currentUserId, pageable);

        return notificationPage.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换实体为VO
     */
    private NotificationVO convertToVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        vo.setId(notification.getId());
        vo.setTitle(notification.getTitle());
        vo.setContent(notification.getContent());
        vo.setDescription(notification.getDescription());
        vo.setType(notification.getType());
        vo.setIcon(notification.getIcon());
        vo.setStatus(notification.getStatus());
        vo.setSenderId(notification.getSenderId());
        vo.setReceiverId(notification.getReceiverId());
        vo.setReceiverType(notification.getReceiverType());
        vo.setPriority(notification.getPriority());
        vo.setImportant(notification.getImportant());
        vo.setExpireTime(notification.getExpireTime());
        vo.setReadTime(notification.getReadTime());
        vo.setCreateTime(notification.getCreateTime());
        vo.setTimeAgo(calculateTimeAgo(notification.getCreateTime()));

        // 解析extraData
        if (StringUtils.hasText(notification.getExtraData())) {
            try {
                Map<String, Object> extraDataMap = objectMapper.readValue(
                        notification.getExtraData(),
                        new TypeReference<Map<String, Object>>() {}
                );
                vo.setExtraData(extraDataMap);
            } catch (JsonProcessingException e) {
                log.error("解析extraData失败", e);
            }
        }

        return vo;
    }

    /**
     * 计算多久前
     */
    private String calculateTimeAgo(LocalDateTime time) {
        if (time == null) {
            return "未知时间";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(time, now);
        long hours = ChronoUnit.HOURS.between(time, now);
        long days = ChronoUnit.DAYS.between(time, now);

        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (days < 7) {
            return days + "天前";
        } else {
            return time.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        }
    }

    /**
     * 根据类型获取图标
     */
    private String getIconByType(String type) {
        Map<String, String> iconMap = new HashMap<>();
        iconMap.put("SYSTEM", "bell");
        iconMap.put("USER", "user");
        iconMap.put("ORDER", "shopping-cart");
        iconMap.put("WARNING", "warning");
        iconMap.put("SUCCESS", "check");
        iconMap.put("INFO", "info");
        return iconMap.getOrDefault(type, "bell");
    }

    /**
     * 根据类型获取优先级
     */
    private Integer getPriorityByType(String type) {
        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("WARNING", 9);
        priorityMap.put("ORDER", 7);
        priorityMap.put("USER", 5);
        priorityMap.put("SYSTEM", 3);
        priorityMap.put("SUCCESS", 2);
        priorityMap.put("INFO", 1);
        return priorityMap.getOrDefault(type, 1);
    }

    /**
     * 判断是否为重要类型
     */
    private Boolean isImportantType(String type) {
        return "WARNING".equals(type) || "ORDER".equals(type);
    }

    /**
     * 获取当前用户ID（这里需要根据你的认证系统实现）
     */
    private Long getCurrentUserId() {
        // TODO: 这里需要从Spring Security中获取当前登录用户ID
        // 暂时返回管理员ID
        return 1L;
    }
}