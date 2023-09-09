package com.example.final_project_17team.global.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "username", timeToLive = 3600 * 24 * 14)
@AllArgsConstructor
@Getter
@Setter
public class Redis {
    @Id
    private String username;
    @Indexed
    private String accessToken;
    @Indexed
    private String refreshToken;
}


// 1. 그냥 로그인 할때
// username 으로 AT, RT 발급

// 2. 만료된 AT 로 요청을 보낼 때
// 프론트에게 만료됬다는 응답을 보냄
// 프론트에서 RT 로 재발급 요청
