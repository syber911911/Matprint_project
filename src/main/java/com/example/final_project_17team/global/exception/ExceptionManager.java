package com.example.final_project_17team.global.exception;

import com.example.final_project_17team.global.dto.ValidationResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestControllerAdvice
@Slf4j
public class ExceptionManager {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> userExceptionHandler(CustomException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handlerResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("path", request.getServletPath());
        response.put("error", ex.getStatusCode());
        response.put("message", ex.getReason());
        response.put("status", ex.getStatusCode().value());
        return new ResponseEntity<>(response, ex.getStatusCode());
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
