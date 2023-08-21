package com.example.final_project_17team.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/*
    ConfigurationProperties를 이용해서 spring.redis의
    하위 값들을 필드로 가져온다.
 */
@Component
@Getter
@Setter
@PropertySource("application.yaml")
public class RedisProperties {
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.host}")
    private String host;

    public int getPort() {
        return this.port;
    }

    public String getHost() {
        return this.host;
    }
}
