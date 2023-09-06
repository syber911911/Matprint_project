package com.example.final_project_17team.restaurant.dto;

import com.example.final_project_17team.global.dto.PlaceDataDto;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestaurantDto {
    private String name;
    private String address;
    private String roadAddress;
    private BigDecimal mapX;
    private BigDecimal mapY;

    public static RestaurantDto fromEntity(Restaurant restaurant) {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName(restaurant.getName());
        restaurantDto.setAddress(restaurant.getAddress());
        restaurantDto.setRoadAddress(restaurant.getRoadAddress());
        restaurantDto.setMapX(restaurant.getMapX());
        restaurantDto.setMapY(restaurant.getMapY());
        return restaurantDto;
    }

    public static RestaurantDto fromDto(PlaceDataDto placeDataDto) {
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName(placeDataDto.getName());
        restaurantDto.setAddress(placeDataDto.getAddress());
        restaurantDto.setRoadAddress(placeDataDto.getRoadAddress());
        restaurantDto.setMapX(placeDataDto.getX());
        restaurantDto.setMapY(placeDataDto.getY());
        return restaurantDto;
    }
}
