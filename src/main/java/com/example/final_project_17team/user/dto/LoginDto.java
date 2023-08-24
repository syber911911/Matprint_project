package com.example.final_project_17team.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class LoginDto {
    @NotBlank(message = "아이디 입력은 필수입니다.")
    private String username;
    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String password;
}
