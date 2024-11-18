package com.web.fitquest.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}
