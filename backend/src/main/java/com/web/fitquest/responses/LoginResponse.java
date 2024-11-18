package com.web.fitquest.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private Integer id;
    private String email;
    private String name;
    private String message;
} 