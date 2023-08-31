package com.example.final_project_17team.restaurant.controller;

import com.example.final_project_17team.dataUpdate.dto.PlaceDataDto;
import com.example.final_project_17team.restaurant.dto.RestaurantDto;
import com.example.final_project_17team.restaurant.service.RestaurantService;
import com.example.final_project_17team.review.dto.ReadReviewDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
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

    // 버튼을 눌렀을 때 위시리스트에 안 들어있으면 위시리스트에 담고, 들어있으면 위시리스트에서 해제하기
    @PostMapping("/wishlist")
    public ResponseEntity<Map<String, String>> wishlist(
            @RequestParam("restaurantId") Long restaurantId
    ) {
        int result = restaurantService.wishlistButton(restaurantId);

        Map<String, String> responseBody = new HashMap<>();
        switch (result) {
            case 1 -> responseBody.put("message", "위시리스트에 담겼습니다.");
            case 2 -> responseBody.put("message", "위시리스트에서 해제 되었습니다.");
            default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(responseBody);
    }
}
