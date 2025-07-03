package com.cass.pinkyBar.service;

import com.cass.pinkyBar.config.JwtUtil;
import com.cass.pinkyBar.dto.LoginRequest;
import com.cass.pinkyBar.dto.LoginResponse;
import com.cass.pinkyBar.dto.RegisterRequest;
import com.cass.pinkyBar.entity.Role;
import com.cass.pinkyBar.entity.User;
import com.cass.pinkyBar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void register_ShouldCreateUser_WhenEmailNotInUse() {
        RegisterRequest req = new RegisterRequest();
        req.setName("John");
        req.setEmail("john@example.com");
        req.setPassword("password123");
        req.setRole(Role.CLIENT);  // <-- ici ton rôle

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("hashedPassword");
        when(jwtUtil.generateToken(req.getEmail(), req.getRole())).thenReturn("token123");

        LoginResponse response = authService.register(req);

        assertEquals("token123", response.getToken());
        assertEquals(Role.CLIENT, response.getRole());
        assertEquals("John", response.getName());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyUsed() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Jane");
        req.setEmail("jane@example.com");
        req.setPassword("password");
        req.setRole(Role.CLIENT);

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        LoginRequest req = new LoginRequest();
        req.setEmail("john@example.com");
        req.setPassword("password123");

        User user = User.builder()
                .name("John")
                .email(req.getEmail())
                .password("hashedPassword")
                .role(Role.CLIENT)
                .build();

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getEmail(), user.getRole())).thenReturn("token123");

        LoginResponse response = authService.login(req);

        assertEquals("token123", response.getToken());
        assertEquals(Role.CLIENT, response.getRole());
        assertEquals("John", response.getName());
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        LoginRequest req = new LoginRequest();
        req.setEmail("notfound@example.com");
        req.setPassword("password");

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authService.login(req));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordInvalid() {
        LoginRequest req = new LoginRequest();
        req.setEmail("john@example.com");
        req.setPassword("wrongpassword");

        User user = User.builder()
                .name("John")
                .email(req.getEmail())
                .password("hashedPassword")
                .role(Role.CLIENT)
                .build();

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getPassword(), user.getPassword())).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authService.login(req));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }
}
