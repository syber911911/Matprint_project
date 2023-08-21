package com.example.final_project_17team.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private boolean gender;
    private Long age;
    private String img_url;
    private LocalDateTime created_at;
    private LocalDateTime modified_at;
}
