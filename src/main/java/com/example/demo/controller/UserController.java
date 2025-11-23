package com.example.demo.controller;

import com.example.demo.dto.UserInfoDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 先全放行，生产再收窄
public class UserController {

    @Resource
    private UserRepository repo;

    @Resource
    private AuthService authService;

    @Resource
    private UserService userService;

    @GetMapping("/users")
    public List<User> all() {
        List<User> users = repo.findAll();
        return users;
    }

    // 分页查询用户
    @GetMapping("/users/page")
    public Page<User> getUsersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return userService.getUsersWithPagination(page, size, search);
    }

    @PostMapping("/addUser")
    public User create(@RequestBody User u) {
        return repo.save(u);
    }

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = repo.findById(id).orElseThrow();
        existingUser.setName(user.getName());
        existingUser.setAge(user.getAge());
        existingUser.setRole(user.getRole());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        return repo.save(existingUser);
    }

    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }

    // 批量删除用户
    @PostMapping("/users/batch-delete")
    public void batchDelete(@RequestBody List<Long> ids) {
        userService.deleteUsers(ids);
    }

    // 检查用户名是否存在
    @GetMapping("/users/check-username")
    public Map<String, Boolean> checkUsername(
            @RequestParam String username,
            @RequestParam(required = false) Long excludeId) {
        boolean exists;
        if (excludeId != null) {
            exists = userService.isUsernameExists(username, excludeId);
        } else {
            exists = userService.isUsernameExists(username);
        }
        return Map.of("exists", exists);
    }

    @GetMapping("/user/info")
    public UserInfoDTO getUserInfo(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = authService.getUserIdByToken(actualToken);
        return userService.getUserInfo(userId);
    }

    @PutMapping("/user/info")
    public UserInfoDTO updateUserInfo(@RequestHeader("Authorization") String token,
                                      @RequestBody User user) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = authService.getUserIdByToken(actualToken);
        User updatedUser = userService.updateUserInfo(userId, user);
        return userService.getUserInfo(updatedUser.getId());
    }
}