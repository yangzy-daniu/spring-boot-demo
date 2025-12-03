package com.example.demo.repository;

import com.example.demo.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long>, JpaSpecificationExecutor<SystemLog> {

    List<SystemLog> findTop10ByOrderByTimestampDesc();

//    Page<SystemLog> findByLevelOrderByTimestampDesc(String level, Pageable pageable);
//
//    List<SystemLog> findByServiceAndTimestampBetween(String service, LocalDateTime start, LocalDateTime end);

    Long countByLevelAndTimestampBetween(String level, LocalDateTime start, LocalDateTime end);

}