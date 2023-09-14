package com.example.final_project_17team.review.controller;

import com.example.final_project_17team.global.dto.ResponseDto;
import com.example.final_project_17team.review.dto.CreateReviewDto;
import com.example.final_project_17team.review.dto.ReadReviewDto;
import com.example.final_project_17team.review.dto.UpdateReviewDto;
import com.example.final_project_17team.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/{restaurantId}/review")
public class ReviewController {
    private ReviewService reviewService;

    // 리뷰작성
    @PostMapping
    public ResponseDto create(
            @AuthenticationPrincipal String username,
            @PathVariable("restaurantId") Long restaurantId,
            @ModelAttribute("review") CreateReviewDto dto
    ) throws IOException {
        return reviewService.createReview(username, restaurantId, dto);
    }

    @GetMapping
    public ReadReviewDto.ReadReviewWithUser readReviews(
            @AuthenticationPrincipal String username,
            @PathVariable("restaurantId") Long restaurantId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer limit
    ){
        return reviewService.readReviewPage(username, restaurantId, page, limit);
    }

    @GetMapping("/{reviewId}")
    public ReadReviewDto readAReviews(@PathVariable("reviewId") Long reviewId){
        return reviewService.readAReview(reviewId);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseDto deleteReview(
            @AuthenticationPrincipal String username,
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("reviewId") Long reviewId
    ) {
        return reviewService.deleteReview(username, restaurantId, reviewId);
    }

    @PutMapping("/{reviewId}")
    public ResponseDto updateReview(
            @AuthenticationPrincipal String username,
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("reviewId") Long reviewId,
            @ModelAttribute @Valid UpdateReviewDto request
    ) throws IOException {
        return reviewService.updateReview(username, restaurantId, reviewId, request);
    }
}
