package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SystemTodoService {

    private final TodoService todoService;
    private final UserRepository userRepository;

    /**
     * 当新用户注册时，自动为管理员创建审核待办
     * @param newUsername 新注册的用户名
     */
    public void createUserApprovalTodo(String newUsername) {
        // 查找所有管理员用户（角色为 admin 或 super）
        List<User> admins = userRepository.findByRoleCodeIn(Arrays.asList("admin", "super"));

        if (admins.isEmpty()) {
            log.warn("未找到管理员用户，无法创建用户审核待办");
            return;
        }

        for (User admin : admins) {
            try {
                todoService.createSystemTodo(
                        admin.getId(),
                        "审核新用户注册申请",
                        "用户 \"" + newUsername + "\" 提交了注册申请，需要审核资质",
                        "HIGH",
                        "USER_APPROVAL_" + newUsername + "_" + System.currentTimeMillis(),
                        LocalDateTime.now().plusHours(2)
                );
                log.info("为用户 {} 创建了审核待办: {}", admin.getUsername(), newUsername);
            } catch (Exception e) {
                log.error("为用户 {} 创建审核待办失败: {}", admin.getUsername(), e.getMessage());
            }
        }
    }

    /**
     * 当有新订单时，自动为相关人员创建处理待办
     * @param orderNo 订单号
     * @param items 商品数量
     */
    public void createOrderProcessingTodo(String orderNo, int items) {
        // 查找订单处理人员（可以是特定角色或部门）
        List<User> orderHandlers = userRepository.findByDepartmentAndPosition("销售部", "客服");

        // 如果没有特定人员，则分配给所有管理员
        if (orderHandlers.isEmpty()) {
            orderHandlers = userRepository.findByRoleCodeIn(Arrays.asList("admin", "super"));
        }

        for (User handler : orderHandlers) {
            try {
                todoService.createSystemTodo(
                        handler.getId(),
                        "处理待发货订单",
                        "订单号: " + orderNo + "，包含 " + items + " 件商品，需要处理发货",
                        "MEDIUM",
                        "ORDER_" + orderNo,
                        LocalDateTime.now().plusHours(4)
                );
                log.info("为用户 {} 创建了订单处理待办: {}", handler.getUsername(), orderNo);
            } catch (Exception e) {
                log.error("为用户 {} 创建订单待办失败: {}", handler.getUsername(), e.getMessage());
            }
        }
    }

    /**
     * 系统更新时，为管理员创建更新确认待办
     * @param version 新版本号
     * @param updateContent 更新内容
     */
    public void createSystemUpdateTodo(String version, String updateContent) {
        List<User> admins = userRepository.findByRoleCodeIn(Arrays.asList("admin", "super"));

        for (User admin : admins) {
            try {
                todoService.createSystemTodo(
                        admin.getId(),
                        "系统更新确认",
                        "系统已更新至版本 " + version + "，更新内容：" + updateContent,
                        "LOW",
                        "SYSTEM_UPDATE_" + version,
                        LocalDateTime.now().plusDays(1)  // 1天内确认
                );
                log.info("为用户 {} 创建了系统更新待办: v{}", admin.getUsername(), version);
            } catch (Exception e) {
                log.error("为用户 {} 创建系统更新待办失败: {}", admin.getUsername(), e.getMessage());
            }
        }
    }

    /**
     * 创建会议提醒待办
     * @param userId 用户ID
     * @param meetingTitle 会议标题
     * @param meetingTime 会议时间
     * @param participants 参会人员
     */
    public void createMeetingReminderTodo(Long userId, String meetingTitle,
                                          LocalDateTime meetingTime, String participants) {
        try {
            todoService.createSystemTodo(
                    userId,
                    "会议：" + meetingTitle,
                    "会议时间：" + meetingTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                            "，参会人员：" + participants,
                    "MEDIUM",
                    "MEETING_" + meetingTitle + "_" + System.currentTimeMillis(),
                    meetingTime.minusMinutes(15)  // 提前15分钟提醒
            );
            log.info("为用户 {} 创建了会议待办: {}", userId, meetingTitle);
        } catch (Exception e) {
            log.error("创建会议待办失败: {}", e.getMessage());
        }
    }

    /**
     * 创建报告提交提醒
     * @param userId 用户ID
     * @param reportName 报告名称
     * @param deadline 截止时间
     */
    public void createReportSubmissionTodo(Long userId, String reportName, LocalDateTime deadline) {
        try {
            todoService.createSystemTodo(
                    userId,
                    "提交报告：" + reportName,
                    reportName + " 需要提交，请按时完成",
                    "HIGH",
                    "REPORT_" + reportName,
                    deadline.minusDays(1)  // 提前1天提醒
            );
            log.info("为用户 {} 创建了报告提交待办: {}", userId, reportName);
        } catch (Exception e) {
            log.error("创建报告提交待办失败: {}", e.getMessage());
        }
    }
}