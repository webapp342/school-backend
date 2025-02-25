package com.schoolmanagement.controller;

import com.schoolmanagement.entity.User;
import com.schoolmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/code/{code}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<User> getUserByCode(@PathVariable String code) {
        return ResponseEntity.ok(userService.findByCode(code));
    }
}