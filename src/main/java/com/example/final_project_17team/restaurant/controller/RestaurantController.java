package com.example.final_project_17team.restaurant.controller;

import com.example.final_project_17team.global.service.CategoryUpdate;
import com.example.final_project_17team.restaurant.dto.RestaurantDetailDto;
import com.example.final_project_17team.restaurant.dto.RestaurantDto;
import com.example.final_project_17team.restaurant.service.RestaurantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final CategoryUpdate categoryUpdate;

    @PostMapping("/input")
    public void input() throws InterruptedException {
        categoryUpdate.searchAndSaveRestaurant();
    }

    // 이영자 맛집 input
    @PostMapping("/inputLYJ")
    public void inputLYJ() throws InterruptedException {
        categoryUpdate.searchAndSaveLYJRestaurant();
    }

    // 또간집 맛집 input
    @PostMapping("/inputPoongJa")
    public void inputPoongJa() throws InterruptedException {
        categoryUpdate.searchAndSavePoongJaRestaurant();
    }

    // 음식점 검색 endPoint
    @GetMapping("/search")
    public Page<RestaurantDto> search(
            @RequestParam("target") String target,
            @RequestParam(value = "page", defaultValue = "0") int pageNum,
            @RequestParam(value = "limit", defaultValue = "10") int pageSize
    )  {
        return restaurantService.searchRestaurant(target, pageNum, pageSize);
    }

    //상세페이지
    @GetMapping("/detail")
    public RestaurantDetailDto detailPage(@RequestParam("name") String name, @RequestParam("address") String address){
        return restaurantService.detailPage(name, address);
    }
}
