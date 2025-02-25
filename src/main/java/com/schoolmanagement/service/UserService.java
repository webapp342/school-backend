package com.schoolmanagement.service;

import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByCode(String code) {
        return userRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("User not found with code: " + code));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}