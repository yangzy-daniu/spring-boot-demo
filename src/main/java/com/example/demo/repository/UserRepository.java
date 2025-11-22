package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * De // 继承 JpaRepository<User, 主键类型>
 */

@Component
public interface UserRepository extends JpaRepository<User, Long> {
}