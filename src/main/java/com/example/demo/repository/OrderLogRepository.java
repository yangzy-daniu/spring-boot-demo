package com.example.demo.repository;

import com.example.demo.entity.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
    List<OrderLog> findByOrderIdOrderByCreateTimeDesc(Long orderId);

    // 添加删除方法
    @Modifying
    @Transactional
    @Query("DELETE FROM OrderLog ol WHERE ol.order.id = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);
}