package com.example.demo.repository;

import com.example.demo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {

    // 查询用户待办事项
    List<Todo> findByUserIdAndCompletedOrderByDueTimeAsc(Long userId, Boolean completed);

    // 分页查询用户待办事项
    Page<Todo> findByUserIdAndCompleted(Long userId, Boolean completed, Pageable pageable);

    // 查询今日到期的待办
    List<Todo> findByUserIdAndDueTimeBetweenAndCompleted(Long userId, LocalDateTime start, LocalDateTime end, Boolean completed);

    // 查询高优先级的待办
    List<Todo> findByUserIdAndPriorityAndCompleted(Long userId, String priority, Boolean completed);

    // 统计用户待办数量
    Long countByUserIdAndCompleted(Long userId, Boolean completed);

}