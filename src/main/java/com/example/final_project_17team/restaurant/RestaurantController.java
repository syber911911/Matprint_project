package com.example.final_project_17team.restaurant;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class RestaurantController {
    private final RestaurantService service;

    // (지역명, 가게 이름을 통한) 음식점 검색
    @GetMapping("/search")
    public List<RestaurantSearchDto> search(
            @RequestParam("target") String target,
            @RequestParam("page") int pageNum
    ) throws ParseException, IOException {
        return service.searchRestaurant(target, pageNum);
    }
}
