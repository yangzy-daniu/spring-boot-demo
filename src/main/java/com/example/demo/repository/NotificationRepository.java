package com.example.demo.repository;

import com.example.demo.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    // 根据接收者ID和状态分页查询
    Page<Notification> findByReceiverIdAndStatusOrderByCreateTimeDesc(Long receiverId, String status, Pageable pageable);

    // 根据接收者ID分页查询
    Page<Notification> findByReceiverIdOrderByCreateTimeDesc(Long receiverId, Pageable pageable);

    // 根据接收者ID和类型查询
    Page<Notification> findByReceiverIdAndTypeOrderByCreateTimeDesc(Long receiverId, String type, Pageable pageable);

    // 统计未读数量
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.receiverId = :receiverId AND n.status = 'UNREAD'")
    long countUnreadByReceiverId(@Param("receiverId") Long receiverId);

    // 标记为已读
    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readTime = :readTime, n.updateTime = :updateTime WHERE n.id = :id")
    int markAsRead(@Param("id") Long id, @Param("readTime") LocalDateTime readTime, @Param("updateTime") LocalDateTime updateTime);

    // 批量标记为已读
    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readTime = :readTime, n.updateTime = :updateTime WHERE n.id IN :ids AND n.receiverId = :receiverId")
    int batchMarkAsRead(@Param("ids") List<Long> ids, @Param("receiverId") Long receiverId,
                        @Param("readTime") LocalDateTime readTime, @Param("updateTime") LocalDateTime updateTime);

    // 标记所有为已读
    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readTime = :readTime, n.updateTime = :updateTime WHERE n.receiverId = :receiverId AND n.status = 'UNREAD'")
    int markAllAsRead(@Param("receiverId") Long receiverId, @Param("readTime") LocalDateTime readTime, @Param("updateTime") LocalDateTime updateTime);

    // 根据重要性和状态查询
    Page<Notification> findByReceiverIdAndImportantAndStatusOrderByCreateTimeDesc(Long receiverId, Boolean important, String status, Pageable pageable);

    // 查找过期的通知
    @Query("SELECT n FROM Notification n WHERE n.expireTime < :now AND n.status != 'ARCHIVED'")
    List<Notification> findExpiredNotifications(@Param("now") LocalDateTime now);

    // 归档过期的通知
    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'ARCHIVED', n.updateTime = :updateTime WHERE n.expireTime < :now AND n.status != 'ARCHIVED'")
    int archiveExpiredNotifications(@Param("now") LocalDateTime now, @Param("updateTime") LocalDateTime updateTime);
}