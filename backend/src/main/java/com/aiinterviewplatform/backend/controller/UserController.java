package com.aiinterviewplatform.backend.controller;

import com.aiinterviewplatform.backend.dto.UserResponse;
import com.aiinterviewplatform.backend.entity.User;
import com.aiinterviewplatform.backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser() {

        User user = userService.getCurrentUser();

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}