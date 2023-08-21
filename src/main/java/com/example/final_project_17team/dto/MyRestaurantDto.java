package com.example.final_project_17team.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyRestaurantDto {
    private Long id;
    private LocalDateTime created_at;
    private LocalDateTime deleted_at;
    private LocalDateTime modified_at;
    private boolean visited;
    private Long user_id;
    private Long restaurant_id;
}
