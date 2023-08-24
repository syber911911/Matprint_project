package com.example.final_project_17team.global.jwt;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JwtTokenDto {
    private String accessToken;
    private String refreshToken;
}
