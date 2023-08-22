package com.example.final_project_17team.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "user name is duplicated"),
    DIFF_PASSWORD_CHECK(HttpStatus.BAD_REQUEST, "password check is different with password");

    private HttpStatus status;
    private String message;
}
