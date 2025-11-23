package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * @author Administrator
 */
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(unique = true, nullable = false)
    private String username;

//    @Column(nullable = false)
    private String password;

    private String name;
    private Integer age;

    // 角色字段，可以扩展为角色表
    private String role = "USER";
}