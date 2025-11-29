// src/main/java/com/example/demo/service/OperationLogService.java
package com.example.demo.service;

import com.example.demo.entity.OperationLog;
import com.example.demo.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    @Async
    public void saveLog(OperationLog operationLog) {
        try {
            operationLog.setCreateTime(LocalDateTime.now());
            operationLogRepository.save(operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    public Page<OperationLog> getLogsByPage(String module, String operator, String type,
                                            String result, LocalDateTime startTime,
                                            LocalDateTime endTime, Pageable pageable) {
        return operationLogRepository.findByConditions(module, operator, type, result,
                startTime, endTime, pageable);
    }

    public List<OperationLog> getRecentLogs() {
        return operationLogRepository.findTop10ByOrderByCreateTimeDesc();
    }
}