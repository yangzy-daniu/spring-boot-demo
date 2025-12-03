package com.example.demo.controller;

import com.example.demo.dto.RecentAccessDTO;
import com.example.demo.service.RecentAccessService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recent-access")
@RequiredArgsConstructor
@Tag(name = "最近访问记录", description = "管理用户的最近访问记录")
public class RecentAccessController {

    private final RecentAccessService recentAccessService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "获取最近访问记录")
    public ResponseEntity<?> getRecentAccess(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = userService.getCurrentUserId(userDetails);
            List<RecentAccessDTO> records = recentAccessService.getRecentAccess(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", records);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "获取最近访问记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/record")
    @Operation(summary = "记录访问")
    public ResponseEntity<?> recordAccess(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody RecordAccessRequest request) {
        try {
            String userId = userService.getCurrentUserId(userDetails);
            recentAccessService.recordAccess(
                    userId,
                    request.getMenuName(),
                    request.getMenuPath(),
                    request.getMenuIcon()
            );

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "记录成功");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "记录访问失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping
    @Operation(summary = "清除最近访问记录")
    public ResponseEntity<?> clearRecentAccess(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = userService.getCurrentUserId(userDetails);
            recentAccessService.clearRecentAccess(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "清除成功");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "清除失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @Data
    public static class RecordAccessRequest {
        private String menuName;
        private String menuPath;
        private String menuIcon;
        private String menuType;
    }
}