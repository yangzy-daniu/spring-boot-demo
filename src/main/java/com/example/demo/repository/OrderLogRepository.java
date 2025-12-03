package com.example.demo.repository;

import com.example.demo.entity.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
    List<OrderLog> findByOrderIdOrderByCreateTimeDesc(Long orderId);

}