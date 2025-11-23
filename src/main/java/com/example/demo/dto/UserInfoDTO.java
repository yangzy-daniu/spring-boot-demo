package com.example.demo.dto;

import lombok.Data;

@Data
public class UserInfoDTO {
    private Long id;
    private String username;
    private String name;
    private String role;
    private String avatar;

    public UserInfoDTO(Long id, String username, String name, String role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
        this.avatar = "/api/avatar/default-avatar.png";
    }
}