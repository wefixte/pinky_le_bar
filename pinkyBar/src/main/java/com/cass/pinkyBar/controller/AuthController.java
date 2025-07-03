package com.cass.pinkyBar.controller;

import com.cass.pinkyBar.dto.LoginRequest;
import com.cass.pinkyBar.dto.LoginResponse;
import com.cass.pinkyBar.dto.RegisterRequest;
import com.cass.pinkyBar.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest registerRequest) {
        LoginResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail()); // <== log ici
        LoginResponse response = authService.login(loginRequest);
        log.info("Login successful for user: {}", loginRequest.getEmail());
        return ResponseEntity.ok(response);
    }
}
