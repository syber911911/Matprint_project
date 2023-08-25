package com.example.final_project_17team.global.jwt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JwtTokenInfoDto {
    private String accessToken;
    private String refreshToken;
    private Boolean logOut;
}
