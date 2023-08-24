package com.example.final_project_17team.global.exception;

import com.example.final_project_17team.global.dto.ValidationResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ExceptionManager {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> userExceptionHandler(CustomException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationResponseDto> handlerValidationException(MethodArgumentNotValidException ex) {
        ValidationResponseDto response = new ValidationResponseDto();
        List<ValidationResponseDto.Content> contentList = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            ValidationResponseDto.Content content = new ValidationResponseDto.Content();
            content.setFieldName(error.getField());
            content.setMessage(error.getDefaultMessage());
            contentList.add(content);
        }
        response.setContentList(contentList);
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
