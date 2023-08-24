package com.example.final_project_17team.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantDto {
    private Long id;
    private String name;
    private String address;
    private float lat;
    private float lon;
    private String phone;
    private float avg_ratings;
    private String runtime;
    private String menu_info;
    private String img_url;
    private LocalDateTime created_at;
    private LocalDateTime deleted_at;
    private LocalDateTime modified_at;
}
