package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/users")
    public List<User> all() {
        List<User> users = repo.findAll();
        System.out.println(users.get(0).getName());
        return users;
    }

    @PostMapping("/addUser")
    public User create(@RequestBody User u) {
        return repo.save(u);
    }

    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}