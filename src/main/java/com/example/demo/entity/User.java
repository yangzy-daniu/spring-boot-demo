package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 */
@Entity
@Table(name = "users")
@NoArgsConstructor   // ← 关键
//@Data
public class User {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private Integer age;

    /* 手工 setter & getter */
    public void setName(String name){ this.name = name; }
    public void setAge(Integer age){ this.age = age; }
    public Long   getId()   { return id; }
    public String getName() { return name; }
    public Integer getAge() { return age; }
}