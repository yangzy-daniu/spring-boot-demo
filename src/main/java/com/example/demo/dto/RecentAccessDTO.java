package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class RecentAccessDTO {
    private Long id;
    private String menuName;
    private String menuPath;
    private String menuIcon;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String accessTime;
    private Integer visitCount;
    private String displayTime;

}