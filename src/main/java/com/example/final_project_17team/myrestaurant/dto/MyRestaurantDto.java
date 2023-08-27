package com.example.final_project_17team.myrestaurant.dto;

import com.example.final_project_17team.myrestaurant.entity.MyRestaurant;
import com.example.final_project_17team.restaurant.entity.Restaurant;
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
    private String restaurant_name;

    public static MyRestaurantDto fromEntity(MyRestaurant myRestaurant) {
        MyRestaurantDto dto = new MyRestaurantDto();

        dto.setVisited(myRestaurant.isVisited());
        Restaurant restaurant = myRestaurant.getRestaurant();
        if (restaurant != null) {
            dto.setRestaurant_name(restaurant.getName());
        }
        return dto;
    }
}
