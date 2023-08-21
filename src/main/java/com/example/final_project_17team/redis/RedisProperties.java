package com.example.final_project_17team.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
    ConfigurationProperties를 이용해서 spring.redis의
    하위 값들을 필드로 가져온다.
 */
@Component
@ConfigurationProperties(prefix = "spring.redis")
@Getter
@Setter
public class RedisProperties {
    private int port;
    private String host;

    public int getPort() {
        return this.port;
    }

    public String getHost() {
        return this.host;
    }
}
