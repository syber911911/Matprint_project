package com.example.final_project_17team.global.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<Redis, String> {
    Optional<Redis> findRedisByUsername(String username);
}
