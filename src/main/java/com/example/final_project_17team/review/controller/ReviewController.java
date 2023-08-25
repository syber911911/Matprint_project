package com.example.final_project_17team.review.controller;

import com.example.final_project_17team.review.dto.ReviewRequestDto;
import com.example.final_project_17team.review.dto.ReviewUpdateDto;
import com.example.final_project_17team.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private ReviewService reviewService;

    // 리뷰작성
    @PostMapping("/post/restaurant/{restaurantId}")
    public ResponseEntity<Map<String, String>> create(
            @PathVariable("restaurantId")Long restaurantId,
            @ModelAttribute("review") ReviewRequestDto dto
    ){

        reviewService.createReview(restaurantId, dto);

        log.info(dto.toString());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "리뷰 등록이 완료되었습니다.");

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/edit/restaurant/{restaurantId}/review/{reviewId}")
    public ResponseEntity<Map<String, String>> updateArticle(
            @PathVariable("restaurantId")Long restaurantId,
            @PathVariable("reviewId")Long reviewId,
            @ModelAttribute("review") ReviewUpdateDto dto
    ) {
        reviewService.updateArticle(restaurantId, reviewId, dto);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "리뷰를 수정했습니다.");
        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/deleteImage/restaurant/{restaurantId}/review/{reviewId}/image/{imageId}")
    public ResponseEntity<Map<String, String>> deleteImage(
            @PathVariable("restaurantId")Long restaurantId,
            @PathVariable("reviewId")Long reviewId,
            @PathVariable("imageId") Long imageId)
    {
        reviewService.deleteImage(restaurantId, reviewId, imageId);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "리뷰에 이미지가 삭제 되었습니다.");
        return ResponseEntity.ok(responseBody);
    }

}
