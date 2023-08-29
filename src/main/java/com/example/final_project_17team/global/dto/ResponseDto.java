package com.example.final_project_17team.global.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
@ToString
public class ResponseDto {
    private String message;
    private HttpStatus status;
}
