package com.example.demo.scheduler;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SystemTodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemTodoScheduler {

    private final SystemTodoService systemTodoService;
    private final UserRepository userRepository;

    /**
     * 每周一早上9点创建周报提交待办
     */
    @Scheduled(cron = "0 0 9 ? * MON")  // 每周一9:00
    public void createWeeklyReportTodo() {
        log.info("开始创建周报提交待办...");

        List<User> allUsers = userRepository.findAll();
        LocalDateTime deadline = LocalDateTime.now()
                .with(DayOfWeek.FRIDAY)  // 周五
                .withHour(18)            // 18:00
                .withMinute(0)
                .withSecond(0);

        for (User user : allUsers) {
            if (!"user".equals(user.getRoleCode())) {
                // 只为普通用户创建周报待办
                systemTodoService.createReportSubmissionTodo(
                        user.getId(),
                        "周报",
                        deadline
                );
            }
        }

        log.info("周报提交待办创建完成，共 {} 个用户", allUsers.size());
    }

    /**
     * 每月1号创建月度报告待办
     */
    @Scheduled(cron = "0 0 9 1 * ?")  // 每月1号9:00
    public void createMonthlyReportTodo() {
        log.info("开始创建月度报告待办...");

        List<User> managers = userRepository.findByRoleCodeIn(
                java.util.Arrays.asList("admin", "manager")
        );

        LocalDateTime deadline = LocalDateTime.now()
                .plusMonths(1)
                .withDayOfMonth(5)  // 下月5号
                .withHour(18)       // 18:00
                .withMinute(0)
                .withSecond(0);

        for (User manager : managers) {
            systemTodoService.createReportSubmissionTodo(
                    manager.getId(),
                    "月度工作报告",
                    deadline
            );
        }

        log.info("月度报告待办创建完成，共 {} 个管理员", managers.size());
    }
}