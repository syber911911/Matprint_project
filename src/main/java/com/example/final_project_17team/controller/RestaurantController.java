package com.example.final_project_17team.controller;

import com.example.final_project_17team.service.RestaurantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@AllArgsConstructor
public class RestaurantController {
    private final RestaurantService service;

    // (지역명, 가게 이름을 통한) 음식점 검색
    @GetMapping("/search")
    public JSONArray search(
            @RequestParam("target") String target
    ) throws ParseException, IOException {
        return service.searchRestaurant(target);
    }
}
