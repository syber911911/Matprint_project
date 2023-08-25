package com.example.final_project_17team.user.controller;

import com.example.final_project_17team.global.exception.ErrorCode;
import com.example.final_project_17team.global.exception.CustomException;
import com.example.final_project_17team.global.jwt.JwtTokenInfoDto;
import com.example.final_project_17team.global.jwt.JwtTokenUtils;
import com.example.final_project_17team.user.dto.CustomUserDetails;
import com.example.final_project_17team.user.dto.JoinDto;
import com.example.final_project_17team.user.dto.LoginDto;
import com.example.final_project_17team.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserService service;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/login")
    // login 시도 시 redis 에서 해당 유저의 refresh token 이 있는지 확인
    // 1. login logic 에서 기존 refresh token 을 지우고 새로 토큰 발급
    // 2. 기존 refresh token 으로 재발급
    public JwtTokenInfoDto login(@RequestBody @Valid LoginDto request, HttpServletResponse response) {
        JwtTokenInfoDto jwtTokenInfoDto = service.loginUser(request);
        Cookie cookie = new Cookie("REFRESH_TOKEN", jwtTokenInfoDto.getRefreshToken());
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(600);
        response.addCookie(cookie);
        return jwtTokenInfoDto;
    }

    @GetMapping("/reissue")
    public JwtTokenInfoDto reissue(@CookieValue("REFRESH_TOKEN") String refreshToken) {
        return jwtTokenUtils.regenratedToken(refreshToken);
    }

    @PostMapping("/join")
    public void join(@RequestBody @Valid JoinDto request) {
        if (!request.getPasswordCheck().equals(request.getPassword()))
            throw new CustomException(ErrorCode.DIFF_PASSWORD_CHECK, String.format("Username : %s", request.getUsername()));
        service.createUser(CustomUserDetails.fromDto(request));
    }
}
