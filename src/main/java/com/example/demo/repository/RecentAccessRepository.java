package com.example.demo.repository;

import com.example.demo.entity.RecentAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RecentAccessRepository extends JpaRepository<RecentAccess, Long> {

    // 根据用户ID查找最近的访问记录（按访问时间倒序）
    List<RecentAccess> findByUserIdOrderByAccessTimeDesc(String userId);

    // 根据用户ID和菜单路径查找记录
    RecentAccess findByUserIdAndMenuPath(String userId, String menuPath);

    // 获取用户需要保留的最新记录ID
    @Query(value = "SELECT id FROM recent_access WHERE user_id = :userId ORDER BY access_time DESC LIMIT :limit",
            nativeQuery = true)
    List<Long> findRecentIdsToKeep(@Param("userId") String userId, @Param("limit") int limit);

    // 删除用户的过期访问记录（使用原生SQL）
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM recent_access WHERE user_id = :userId AND id NOT IN (:keepIds)",
            nativeQuery = true)
    void deleteOldRecords(@Param("userId") String userId, @Param("keepIds") List<Long> keepIds);

}