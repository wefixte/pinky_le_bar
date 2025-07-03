package com.cass.pinkyBar.dto;

import com.cass.pinkyBar.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class LoginResponse {
    private String token;
    private Role role;
    private String name;

    public LoginResponse(String token, Role role, String name) {
        this.token = token;
        this.role = role;
        this.name = name;
        log.info("LoginResponse created for user: {}", name);
    }
}
