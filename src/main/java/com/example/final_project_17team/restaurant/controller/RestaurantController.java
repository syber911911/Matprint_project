package com.example.final_project_17team.restaurant.controller;

import com.example.final_project_17team.dataUpdate.service.CategoryUpdate;
import com.example.final_project_17team.restaurant.dto.RestaurantSearchDto;
import com.example.final_project_17team.restaurant.service.RestaurantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/search")
    public List<RestaurantSearchDto> search(
            @RequestParam("target") String target,
            @RequestParam("page") int pageNum
    ) throws ParseException, IOException {
        return service.searchRestaurant(target, pageNum);
    }



    @DeleteMapping("/article/{articleId}/comment/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam("commentId") Long commentId
    ) {
        if (service.deleteReview(restaurantId,commentId)) {

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "댓글을 삭제했습니다.");

            return ResponseEntity.ok(responseBody);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
