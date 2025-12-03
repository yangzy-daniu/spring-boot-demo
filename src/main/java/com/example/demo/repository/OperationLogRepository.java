package com.example.demo.repository;

import com.example.demo.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long>, JpaSpecificationExecutor<OperationLog> {

    List<OperationLog> findTop10ByOrderByCreateTimeDesc();

    // 统计用户指定时间范围内的操作数量
    Long countByOperatorIdAndCreateTimeBetween(Long operatorId, LocalDateTime start, LocalDateTime end);

    // 统计用户指定时间范围内的访问数量
    Long countByOperatorIdAndAccessTimeBetween(Long operatorId, LocalDateTime start, LocalDateTime end);

    // 统计用户指定时间范围内且指定结果的操作数量
    Long countByOperatorIdAndResultAndCreateTimeBetween(Long operatorId, String result, LocalDateTime start, LocalDateTime end);

    // 按模块统计操作数量
    Long countByOperatorIdAndModuleAndCreateTimeBetween(Long operatorId, String module, LocalDateTime start, LocalDateTime end);

    // 按模块和结果统计操作数量
    Long countByOperatorIdAndModuleAndResultAndCreateTimeBetween(Long operatorId, String module, String result, LocalDateTime start, LocalDateTime end);

    // 查询用户最近的操作记录
    List<OperationLog> findTop5ByOperatorIdOrderByCreateTimeDesc(Long operatorId);
}