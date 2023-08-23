package com.example.final_project_17team.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@JsonIgnoreProperties(ignoreUnknown=true)
public class RestaurantSearchDto {
    private String name;
    private String tel;
    @Setter
    private String businessHours;
    private String roadAddress;
    private BigDecimal x;
    private BigDecimal y;
}
