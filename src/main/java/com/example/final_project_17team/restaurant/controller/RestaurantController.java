package com.example.final_project_17team.restaurant.controller;

import com.example.final_project_17team.dataUpdate.service.CategoryUpdate;
import com.example.final_project_17team.global.exception.CustomException;
import com.example.final_project_17team.global.exception.ErrorCode;
import com.example.final_project_17team.restaurant.dto.RestaurantSearchDto;
import com.example.final_project_17team.restaurant.service.RestaurantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@AllArgsConstructor
public class RestaurantController {
    private final RestaurantService service;
    private final CategoryUpdate categoryUpdate;

    @PostMapping("/test")
    public void test() throws Exception {
        categoryUpdate.insertPlaceData();
    }

    // (지역명, 가게 이름을 통한) 음식점 검색
    // pageNumber 0 ~ 9, 한 페이지에 음식점 10개씩
    // 반환 된 음식점 수에 따라 더 적을 수 있음
    @GetMapping("/search")
    public Page<RestaurantSearchDto> search(
            @RequestParam("target") String target,
            @RequestParam("page") int pageNum
    ) throws IOException {
        if (pageNum < 0 || pageNum > 9)
            throw new CustomException(ErrorCode.PAGE_NUMBER_OUT_OF_BOUNDS, "The page number must be between 1 n 10");
        return service.searchRestaurant(target, pageNum);
    }
}
