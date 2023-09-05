package com.example.final_project_17team.review.controller;

import com.example.final_project_17team.global.dto.ResponseDto;
import com.example.final_project_17team.review.dto.CreateReviewDto;
import com.example.final_project_17team.review.dto.ReadReviewDto;
import com.example.final_project_17team.review.dto.UpdateReviewDto;
import com.example.final_project_17team.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private ReviewService reviewService;

    // 리뷰작성
    @PostMapping("/{restaurantId}")
    public ResponseDto create(
            @AuthenticationPrincipal String username,
            @PathVariable("restaurantId") Long restaurantId,
            @ModelAttribute("review") CreateReviewDto dto
    ){
        return reviewService.createReview(username, restaurantId, dto);
    }

    @GetMapping("/{restaurantId}")
    public ReadReviewDto.ReadReviewWithUser readReviews(
            @AuthenticationPrincipal String username,
            @PathVariable("restaurantId") Long restaurantId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer limit
    ){
        return reviewService.readReviewPage(username, restaurantId, page, limit);
    }

    @DeleteMapping("/{restaurantId}/{reviewId}")
    public ResponseDto deleteReview(
            @AuthenticationPrincipal String username,
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("reviewId") Long reviewId
    ) {
        return reviewService.deleteReview(username, restaurantId, reviewId);
    }

    @PutMapping("/{restaurantId}/{reviewId}")
    public ResponseDto updateReview(
            @AuthenticationPrincipal String username,
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("reviewId") Long reviewId,
            @ModelAttribute @Valid UpdateReviewDto request
    ) {
        return reviewService.updateReview(username, restaurantId, reviewId, request);
    }
}
