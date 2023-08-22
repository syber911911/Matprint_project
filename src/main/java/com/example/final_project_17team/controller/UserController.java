package com.example.final_project_17team.controller;

import com.example.final_project_17team.dto.UserDto;
import com.example.final_project_17team.exception.ErrorCode;
import com.example.final_project_17team.exception.UserException;
import com.example.final_project_17team.jwt.JwtRequestDto;
import com.example.final_project_17team.jwt.JwtTokenDto;
import com.example.final_project_17team.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public JwtTokenDto login(@RequestBody JwtRequestDto dto) {
        return service.loginUser(dto);
    }

    @PostMapping("/join")
    public void register(@RequestBody UserDto dto, @RequestBody String passwordCheck) {
        if (!passwordCheck.equals(dto.getPassword()))
            throw new UserException(ErrorCode.DIFF_PASSWORD_CHECK, String.format("Username : ", dto.getUsername()));

        service.createUser(UserDto.builder()
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .email(dto.getEmail())
                    .phone(dto.getPhone())
                    .gender(dto.isGender())
                    .age(dto.getAge())
                    .created_at(LocalDateTime.now())
                    .build()
        );
    }
}
