package com.example.final_project_17team.restaurant.controller;

import com.example.final_project_17team.dataUpdate.service.CategoryUpdate;
import com.example.final_project_17team.restaurant.dto.RestaurantDto;
import com.example.final_project_17team.restaurant.dto.RestaurantSearchDto;
import com.example.final_project_17team.restaurant.service.RestaurantService;
import com.example.final_project_17team.review.dto.ReviewPageDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    ) throws IOException {
        return service.searchRestaurant(target, pageNum);
    }

    //상세페이지
    @GetMapping("/detail")
    public RestaurantDto detailPage(@RequestParam("id") Long id){
        return service.detailPage(id);
    }

    @GetMapping("/read")
    public Page<ReviewPageDto> readReviews(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer limit
    ){
        return service.readReviewPage(restaurantId, page, limit);
    }

    @DeleteMapping("/review")
    public ResponseEntity<Map<String, String>> deleteComment(
            @RequestParam("restaurantId") Long restaurantId,
            @RequestParam("reviewId") Long reviewId
    ) {
        if (service.deleteReview(restaurantId, reviewId)) {

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "리뷰를 삭제했습니다.");

            return ResponseEntity.ok(responseBody);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    // 버튼을 눌렀을 때 위시리스트에 안 들어있으면 위시리스트에 담고, 들어있으면 위시리스트에서 해제하기
    @PostMapping("/wishlist")
    public ResponseEntity<Map<String, String>> wishlist(
            @RequestParam("restaurantId") Long restaurantId
    ) {
        int result = service.wishlistButton(restaurantId);

        Map<String, String> responseBody = new HashMap<>();
        switch (result) {
            case 1 -> responseBody.put("message", "위시리스트에 담겼습니다.");
            case 2 -> responseBody.put("message", "위시리스트에서 해제 되었습니다.");
            default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(responseBody);
    }
}
