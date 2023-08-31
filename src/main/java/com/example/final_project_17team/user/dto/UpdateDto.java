package com.example.final_project_17team.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UpdateDto {
    private String username;
    private String password;
    private String email;
    private String phone;
    private boolean gender;
    private Integer age;
    private String imgUrl;

}
