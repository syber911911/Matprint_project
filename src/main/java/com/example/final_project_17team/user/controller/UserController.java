package com.example.final_project_17team.user.controller;

import com.example.final_project_17team.global.exception.ErrorCode;
import com.example.final_project_17team.global.exception.CustomException;
import com.example.final_project_17team.global.jwt.JwtTokenDto;
import com.example.final_project_17team.user.dto.CustomUserDetails;
import com.example.final_project_17team.user.dto.JoinDto;
import com.example.final_project_17team.user.dto.LoginDto;
import com.example.final_project_17team.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public JwtTokenDto login(@RequestBody LoginDto request) {
        return service.loginUser(request);
    }

    @PostMapping("/join")
    public void join(@RequestBody JoinDto request) {
        if (!request.getPasswordCheck().equals(request.getPassword()))
            throw new CustomException(ErrorCode.DIFF_PASSWORD_CHECK, String.format("Username : %s", request.getUsername()));

        service.createUser(CustomUserDetails.fromDto(request));
    }
}
