package com.example.final_project_17team.wishlist.dto;

import com.example.final_project_17team.wishlist.entity.Wishlist;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WishlistDto {
    private String restaurantName;
    private String address;
    private String roadAddress;
    private Float avgRatings;

    public static List<WishlistDto> fromEntityList(List<Wishlist> entityList) {
        List<WishlistDto> wishlistDtoList = new ArrayList<>();
        if (entityList.isEmpty())
            return null;

        for (Wishlist wishlist : entityList) {
            WishlistDto wishlistDto = new WishlistDto();
            wishlistDto.setRestaurantName(wishlist.getRestaurant().getName());
            wishlistDto.setAddress(wishlist.getRestaurant().getAddress());
            wishlistDto.setRoadAddress(wishlist.getRestaurant().getRoadAddress());
            wishlistDto.setAvgRatings(wishlist.getRestaurant().getAvgRatings());
            wishlistDtoList.add(wishlistDto);
        }
        return wishlistDtoList;
    }
}
