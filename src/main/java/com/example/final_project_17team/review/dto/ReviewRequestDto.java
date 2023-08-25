package com.example.final_project_17team.review.dto;

import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.reviewImages.entity.ReviewImages;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewRequestDto {

    private String title;
    private String content;
    private Long ratings;
    private List<MultipartFile> imageList;
    private List<String> imageUrl;

    public static ReviewRequestDto fromEntity(Review review) {
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setRatings(review.getRatings());
        dto.setTitle(review.getTitle());
        dto.setContent(review.getContent());
        List<String> urlList = new ArrayList<>();
        for (ReviewImages reviewImages : review.getReviewImages()) {
            urlList.add(reviewImages.getImage_url());
        }
        dto.setImageUrl(urlList);
        return dto;
    }
}
