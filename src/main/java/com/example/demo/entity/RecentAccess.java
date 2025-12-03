package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "recent_access")
@Data
public class RecentAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(name = "menu_path", nullable = false, length = 200)
    private String menuPath;

    @Column(name = "menu_icon", length = 100)
    private String menuIcon;

    @Column(name = "menu_type", length = 20)
    private String menuType = "MENU";

    @Column(name = "access_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accessTime;

    @Column(name = "visit_count")
    private Integer visitCount = 1;

    @Column(name = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @PrePersist
    protected void onCreate() {
        accessTime = LocalDateTime.now();
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
        if (visitCount == null) {
            visitCount = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}