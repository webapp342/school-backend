package com.schoolmanagement.service;

import com.schoolmanagement.dto.AuthRequest;
import com.schoolmanagement.dto.AuthResponse;
import com.schoolmanagement.entity.Role;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.UserRepository;
import com.schoolmanagement.security.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostConstruct
    public void init() {
        // Create default super admin if not exists
        if (!userRepository.existsByUsername("superadmin")) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("admin123"));
            superAdmin.setCode("SUPER_ADMIN_001");
            superAdmin.setRole(Role.SUPER_ADMIN);
            userRepository.save(superAdmin);
        }
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole())
                .build();
    }
}