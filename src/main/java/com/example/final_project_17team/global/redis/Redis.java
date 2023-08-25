package com.example.final_project_17team.global.redis;

import com.example.final_project_17team.global.jwt.JwtTokenInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "username", timeToLive = 60)
@AllArgsConstructor
@Getter
public class Redis {
    @Id
    private String username;
    private JwtTokenInfoDto jwtTokenInfo;
}
