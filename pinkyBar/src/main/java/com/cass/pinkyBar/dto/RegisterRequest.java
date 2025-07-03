package com.cass.pinkyBar.dto;

import com.cass.pinkyBar.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}