package com.example.demo.dto;

import lombok.Data;

@Data
public class CheckUpdateResponse {
    private Boolean hasUpdate;
    private String currentVersion;
    private String latestVersion;
    private SystemUpdateDTO latestUpdate;
    private Boolean forceUpdate;
    private String message;
}