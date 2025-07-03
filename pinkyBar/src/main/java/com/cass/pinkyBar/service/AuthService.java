package com.cass.pinkyBar.service;

import com.cass.pinkyBar.dto.LoginRequest;
import com.cass.pinkyBar.dto.LoginResponse;
import com.cass.pinkyBar.dto.RegisterRequest;
import com.cass.pinkyBar.entity.User;
import com.cass.pinkyBar.repository.UserRepository;
import com.cass.pinkyBar.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;


import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        String hashedPassword = passwordEncoder.encode(req.getPassword());

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(hashedPassword)
                .role(req.getRole())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new LoginResponse(token, user.getRole(), user.getName());
    }

    public LoginResponse login(LoginRequest req) {
        log.info("Authenticating user with email: {}", req.getEmail());
        Optional<User> userOpt = userRepository.findByEmail(req.getEmail());

        if (userOpt.isEmpty()) {
            log.warn("User not found: {}", req.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", req.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        log.info("User authenticated successfully: {}", req.getEmail());
        return new LoginResponse(token, user.getRole(), user.getName());
    }
}
