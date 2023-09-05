package com.example.final_project_17team.review.dto;

import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.reviewImages.entity.ReviewImages;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReadReviewDto {

    private String username;
    private String content;
    private Integer ratings;
    private List<String> imageUrl;

    public static ReadReviewDto fromEntity(Review review){
        ReadReviewDto dto = new ReadReviewDto();
        dto.setUsername(review.getUser().getUsername());
        dto.setRatings(review.getRatings());
        dto.setContent(review.getContent());

        List<String> urlList = new ArrayList<>();
        for (ReviewImages reviewImages : review.getReviewImages()) {
            urlList.add(reviewImages.getImageUrl());
        }
        dto.setImageUrl(urlList);
        return dto;
    }

    @Data
    public static class ReadReviewWithUser {
        private String accessUsername;
        private Page<ReadReviewDto> reviewPage;
    }

}
