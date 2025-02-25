package com.schoolmanagement.service;

import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            log.info("Loading user by username: {}", username);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("User not found with username: {}", username);
                        return new UsernameNotFoundException("User not found");
                    });

            String role = "ROLE_" + user.getRole().name();
            log.info("User found with role: {}", role);

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(role)));
        } catch (Exception e) {
            log.error("Error loading user by username: {}", username, e);
            throw e;
        }
    }
}