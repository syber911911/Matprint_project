package com.example.final_project_17team.restaurant.dto;

import com.example.final_project_17team.category.entity.Category;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.entity.RestaurantImage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantDetailDto {
    private Long id;
    private String name;
    private String tel;
    private String openHours;
    private String closeHours;
    private String address;
    private String roadAddress;
    private String menuInfo;
    private BigDecimal mapX;
    private BigDecimal mapY;
    private Float avgRatings;
    private List<String> restaurantImageList;
    private List<String> categoryList;

    public static RestaurantDetailDto fromEntity(Restaurant restaurant) {
        List<String> imageList = new LinkedList<>();
        for (RestaurantImage restaurantImage : restaurant.getRestaurantImageList()) {
            imageList.add(restaurantImage.getUrl());
        }

        List<String> categoryList = new LinkedList<>();
        for (Category category : restaurant.getCategoryList()) {
            categoryList.add(category.getName());
        }

        return RestaurantDetailDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .tel(restaurant.getTel())
                .openHours(restaurant.getOpenHours())
                .closeHours(restaurant.getCloseHours())
                .address(restaurant.getAddress())
                .roadAddress(restaurant.getRoadAddress())
                .menuInfo(restaurant.getMenuInfo())
                .mapX(restaurant.getMapX())
                .mapY(restaurant.getMapY())
                .avgRatings(restaurant.getAvgRatings() == null ? (float) 0 : Math.round(restaurant.getAvgRatings() * 10.0f) / 10.0f)
                .restaurantImageList(imageList)
                .categoryList(categoryList)
                .build();
    }
}
