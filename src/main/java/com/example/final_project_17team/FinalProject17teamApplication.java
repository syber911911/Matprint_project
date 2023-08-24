package com.example.final_project_17team;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FinalProject17teamApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalProject17teamApplication.class, args);
    }

}
