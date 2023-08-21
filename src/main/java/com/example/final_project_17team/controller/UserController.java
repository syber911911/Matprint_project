package com.example.final_project_17team.controller;

import com.example.final_project_17team.jwt.JwtRequestDto;
import com.example.final_project_17team.jwt.JwtTokenDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("user")
@AllArgsConstructor
public class UserController {

    @PostMapping("/login")
    public JwtTokenDto login(@RequestBody JwtRequestDto dto) {
        return null;
    }
}
