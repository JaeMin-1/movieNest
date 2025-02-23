package com.movie.movienest.domain.user.controller;

import com.movie.movienest.domain.user.dto.request.LoginRequest;
import com.movie.movienest.domain.user.dto.request.SignupRequest;
import com.movie.movienest.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        String accessToken = userService.login(request, response);
        return ResponseEntity.ok(accessToken); // 로그인 후 Access Token 반환
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        userService.logout(response);
        return ResponseEntity.ok("로그아웃 완료!");
    }
}
