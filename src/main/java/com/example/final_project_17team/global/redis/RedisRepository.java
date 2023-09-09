package com.example.final_project_17team.global.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisRepository extends CrudRepository<Redis, String> {
//    Optional<Redis> findRedisByUsername(String username);
//    Boolean existsByUsername(String username);
    Optional<Redis> findRedisByRefreshToken(String refreshToken);
    Boolean existsByRefreshToken(String refreshToken);
    Boolean existsByAccessToken(String accessToken);
}
