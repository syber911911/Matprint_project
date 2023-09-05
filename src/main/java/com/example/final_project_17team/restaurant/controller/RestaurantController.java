package com.example.final_project_17team.restaurant.controller;

import com.example.final_project_17team.global.dto.PlaceDataDto;
import com.example.final_project_17team.restaurant.dto.RestaurantDto;
import com.example.final_project_17team.restaurant.service.RestaurantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;

    // 음식점 검색 endPoint
    @GetMapping("/search")
    public Page<PlaceDataDto> search(
            @RequestParam("target") String target,
            @RequestParam(value = "page", defaultValue = "0") int pageNum,
            @RequestParam(value = "limit", defaultValue = "10") int pageSize
    )  {
        return restaurantService.searchRestaurant(target, pageNum, pageSize);
    }

    //상세페이지
    @GetMapping("/detail")
    public RestaurantDto detailPage(@RequestParam("name") String name, @RequestParam("address") String address, @RequestParam("mapX") BigDecimal mapX, @RequestParam("mapY") BigDecimal mapY){
        return restaurantService.detailPage(name, address, mapX, mapY);
    }
}
