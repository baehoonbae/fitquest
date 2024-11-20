package com.web.fitquest.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.exception.InvalidTokenException;
import com.web.fitquest.model.user.User;
import com.web.fitquest.requests.LoginRequest;
import com.web.fitquest.requests.RefreshTokenRequest;
import com.web.fitquest.responses.LoginResponse;
import com.web.fitquest.responses.TokenResponse;
import com.web.fitquest.service.token.TokenService;
import com.web.fitquest.service.user.UserService;
import com.web.fitquest.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/regist")
    public ResponseEntity<?> regist(@RequestBody User user) {
        try {
            boolean success = userService.regist(user);
            if (success) {
                return new ResponseEntity<User>(user, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            // log.error("회원가입 서버오류");
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("loginRequest: {}", loginRequest);
        try {
            Optional<User> opUser = userService.login(loginRequest);
            return opUser
                    .map(user -> {
                        TokenResponse tokens = tokenService.createTokens(user.getId());

                        ResponseCookie refreshTokenCookie = ResponseCookie
                                .from("refreshToken", tokens.getRefreshToken())
                                .httpOnly(true)         // 자바스크립트 접근 불가
                                .secure(false)             // https 프로토콜에서만 전송(개발 단계에서는 주석 처리)
                                .path("/")                  // 모든 경로에서 접근 가능
                                .maxAge(60 * 60 * 24 * 14)       // 2주
                                .sameSite("Lax")     // CSRF 방지
                                .domain("")      // 도메인 설정
                                .build();

                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                                .body(new LoginResponse(
                                        tokens.getAccessToken(),
                                        user.getId(),
                                        user.getEmail(),
                                        user.getName(),
                                        "로그인 성공!"));
                    })
                    .orElse(ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body(new LoginResponse(null, null, null, null, "로그인 실패")));
        } catch (Exception e) {
            log.error("Login error: ", e);  // 상세 로그 추가
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(null, null, null, null, "서버 오류"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("리프레시 토큰이 필요합니다.");
            }

            TokenResponse tokens = tokenService.refreshAccessToken(request.getRefreshToken());
            return ResponseEntity.ok(tokens);
        } catch (InvalidTokenException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("토큰 갱신 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7); // "Bearer " 제거
            int userId = jwtUtil.getUserId(token);
            tokenService.revokeRefreshToken(userId);
            return ResponseEntity.ok().body("로그아웃 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그아웃 실패");
        }
    }

    @GetMapping("/check-name/{name}")
    public ResponseEntity<?> checkNameDuplicated(@PathVariable String name) {
        try {
            Optional<User> opUser = userService.selectUserByName(name);
            return opUser
                    .map(user -> {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("사용할 수 없는 닉네임입니다.");
                    })
                    .orElse(ResponseEntity.status(HttpStatus.OK).body("사용 가능한 닉네임입니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmailDuplicated(@PathVariable String email) {
        try {
            Optional<User> opUser = userService.selectUserByEmail(email);
            return opUser
                    .map(user -> {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("사용할 수 없는 이메일입니다.");
                    })
                    .orElse(ResponseEntity.status(HttpStatus.OK).body("사용 가능한 이메일입니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable Integer userId) {
        try {
            Optional<User> opUser = userService.getUserInfo(userId);
            return opUser
                    .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserInfo(@PathVariable Integer userId, @RequestBody User user) {
        try {
            boolean success = userService.updateUser(user);
            if(success) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/check-refresh-token")
    public ResponseEntity<?> checkRefreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        return ResponseEntity.ok()
            .body(Map.of("exists", refreshToken != null));
    }
}
