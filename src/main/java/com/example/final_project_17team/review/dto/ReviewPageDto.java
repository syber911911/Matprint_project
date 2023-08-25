package com.example.final_project_17team.review.dto;

import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.reviewImages.entity.ReviewImages;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReviewPageDto {

    private String Name;
    private String title;
    private String content;
    private Long ratings;
    private List<String> imageUrl;

    public static ReviewPageDto fromEntity(Review review){
        ReviewPageDto dto = new ReviewPageDto();
        dto.setTitle(review.getTitle());
        dto.setName(review.getUser().getUsername());
        dto.setRatings(review.getRatings());
        dto.setContent(review.getContent());

        List<String> urlList = new ArrayList<>();
        for (ReviewImages reviewImages : review.getReviewImages()) {
            urlList.add(reviewImages.getImage_url());
        }
        dto.setImageUrl(urlList);
        return dto;
    }

}
