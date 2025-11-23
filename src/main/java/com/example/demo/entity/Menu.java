package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menus")
@Data
@EqualsAndHashCode(exclude = "children")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;        // 菜单名称

    private String path;        // 路由路径
    private String icon;        // 图标
    private Integer sort = 0;   // 排序

    @Column(name = "parent_id")
    private Long parentId;      // 父菜单ID

    private String component;   // 组件路径

    // 菜单类型：0-目录，1-菜单，2-按钮
    private Integer type = 0;

    @Transient
    private List<Menu> children = new ArrayList<>();
}