package com.example.final_project_17team.restaurant.dto;

import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.entity.RestaurantImage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantDto {
    private Long id;
    private String name;
    private String address;
    //private float lat;
    //private float lon;
    private String tel;
    private float avg_ratings;
    private String runtime;
    private String menu_info;
    private String img_url;
    private LocalDateTime created_at;
    private LocalDateTime deleted_at;
    private LocalDateTime modified_at;

    public static RestaurantDto fromEntity(Restaurant entity) {
        RestaurantDto dto = new RestaurantDto();
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setTel(entity.getTel());

        //평점
        Float avgRatings = entity.getAvgRatings();
        if (avgRatings != null) {
            dto.setAvg_ratings(avgRatings);
        } else {
            dto.setAvg_ratings(0.0f);
        }

        // 영업시간
        String openHours = entity.getOpenHours();
        String closeHours = entity.getCloseHours();
        if (openHours != null && closeHours != null) {
            LocalTime openTime = LocalTime.parse(openHours, DateTimeFormatter.ofPattern("HHmm"));
            LocalTime closeTime = LocalTime.parse(closeHours, DateTimeFormatter.ofPattern("HHmm"));
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String runtime = openTime.format(outputFormatter) + " ~ " + closeTime.format(outputFormatter);
            dto.setRuntime(runtime);
        }

        dto.setMenu_info(entity.getMenuInfo());

        //이미지 url
        List<RestaurantImage> restaurantImages = entity.getRestaurantImageList();
        if (!restaurantImages.isEmpty()) {
            dto.setImg_url(restaurantImages.get(0).getUrl());
        }

        return dto;
    }
}
