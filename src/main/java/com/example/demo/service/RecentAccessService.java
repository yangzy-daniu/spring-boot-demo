package com.example.demo.service;

import com.example.demo.dto.RecentAccessDTO;
import com.example.demo.entity.RecentAccess;
import com.example.demo.repository.RecentAccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecentAccessService {

    private final RecentAccessRepository recentAccessRepository;

    // 最大保留记录数
    private static final int MAX_RECORDS = 10;

    /**
     * 记录访问
     */
    @Transactional
    public void recordAccess(String userId, String menuName, String menuPath, String menuIcon) {
        try {
            // 查找是否已有相同路径的记录
            RecentAccess existing = recentAccessRepository.findByUserIdAndMenuPath(userId, menuPath);

            if (existing != null) {
                // 更新现有记录
                existing.setAccessTime(LocalDateTime.now());
                existing.setVisitCount(existing.getVisitCount() + 1);
                existing.setUpdatedTime(LocalDateTime.now());
                recentAccessRepository.save(existing);
            } else {
                // 创建新记录
                RecentAccess recentAccess = new RecentAccess();
                recentAccess.setUserId(userId);
                recentAccess.setMenuName(menuName);
                recentAccess.setMenuPath(menuPath);
                recentAccess.setMenuIcon(menuIcon);
                recentAccess.setAccessTime(LocalDateTime.now());
                recentAccess.setVisitCount(1);
                recentAccess.setCreatedTime(LocalDateTime.now());
                recentAccess.setUpdatedTime(LocalDateTime.now());
                recentAccessRepository.save(recentAccess);
            }

            // 清理旧记录（保留最近MAX_RECORDS条）
            cleanupOldRecords(userId);

        } catch (Exception e) {
            log.error("记录访问历史失败: userId={}, menu={}", userId, menuName, e);
            // 记录失败不影响主要功能
        }
    }

    /**
     * 清理旧记录
     */
    private void cleanupOldRecords(String userId) {
        try {
            // 方法1：获取需要保留的ID列表，然后删除其他的
            List<Long> keepIds = recentAccessRepository.findRecentIdsToKeep(userId, MAX_RECORDS);
            if (keepIds != null && !keepIds.isEmpty()) {
                recentAccessRepository.deleteOldRecords(userId, keepIds);
            }

            // 或者方法2：保留最近N条，删除更早的
            // cleanupByCutoffTime(userId);

        } catch (Exception e) {
            log.error("清理旧记录失败: userId={}", userId, e);
        }
    }

    /**
     * 方法2：按截止时间清理
     */
    private void cleanupByCutoffTime(String userId) {
        try {
            // 获取用户的最近MAX_RECORDS条记录
            List<RecentAccess> recentRecords = recentAccessRepository
                    .findByUserIdOrderByAccessTimeDesc(userId);

            if (recentRecords.size() > MAX_RECORDS) {
                // 找到第MAX_RECORDS条记录的访问时间
                RecentAccess cutoffRecord = recentRecords.get(MAX_RECORDS - 1);
                LocalDateTime cutoffTime = cutoffRecord.getAccessTime();

                // 删除早于这个时间的记录
                List<RecentAccess> oldRecords = recentRecords.stream()
                        .filter(record -> record.getAccessTime().isBefore(cutoffTime))
                        .collect(Collectors.toList());

                recentAccessRepository.deleteAll(oldRecords);
            }
        } catch (Exception e) {
            log.error("按时间清理记录失败", e);
        }
    }

    /**
     * 获取用户的最近访问记录
     */
    public List<RecentAccessDTO> getRecentAccess(String userId) {
        try {
            List<RecentAccess> records = recentAccessRepository.findByUserIdOrderByAccessTimeDesc(userId);

            // 限制返回数量（例如最近5条用于展示）
            int displayCount = Math.min(5, records.size());
            records = records.subList(0, displayCount);

            return records.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取最近访问记录失败: userId={}", userId, e);
            return List.of(); // 返回空列表而不是抛出异常
        }
    }

    /**
     * 清除用户的访问记录
     */
    @Transactional
    public void clearRecentAccess(String userId) {
        try {
            List<RecentAccess> records = recentAccessRepository.findByUserIdOrderByAccessTimeDesc(userId);
            recentAccessRepository.deleteAll(records);
            log.info("已清除用户 {} 的访问记录，共 {} 条", userId, records.size());
        } catch (Exception e) {
            log.error("清除访问记录失败: userId={}", userId, e);
            throw new RuntimeException("清除访问记录失败", e);
        }
    }

    /**
     * 转换为DTO
     */
    private RecentAccessDTO convertToDTO(RecentAccess entity) {
        RecentAccessDTO dto = new RecentAccessDTO();
        dto.setId(entity.getId());
        dto.setMenuName(entity.getMenuName());
        dto.setMenuPath(entity.getMenuPath());
        dto.setMenuIcon(entity.getMenuIcon());
        dto.setVisitCount(entity.getVisitCount());
        dto.setAccessTime(entity.getAccessTime() != null ?
                entity.getAccessTime().toString() : "");

        // 计算显示时间
        if (entity.getAccessTime() != null) {
            dto.setDisplayTime(formatDisplayTime(entity.getAccessTime()));
        }

        return dto;
    }

    /**
     * 格式化显示时间
     */
    private String formatDisplayTime(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(time, now).getSeconds();

        if (seconds < 60) {
            return "刚刚";
        } else if (seconds < 3600) {
            return (seconds / 60) + "分钟前";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "小时前";
        } else if (seconds < 2592000) {
            return (seconds / 86400) + "天前";
        } else {
            return time.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        }
    }
}