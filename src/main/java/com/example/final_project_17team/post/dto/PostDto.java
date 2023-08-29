package com.example.final_project_17team.post.dto;

import com.example.final_project_17team.post.entity.Post;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.review.dto.ReviewPageDto;
import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.reviewImages.entity.ReviewImages;
import com.example.final_project_17team.user.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String userName;
    private String status;
    private LocalDateTime visitDate;
    private String prefer;
    private Long userId;
    private Long restaurantId;

    public static PostDto fromEntity(Post post){
        PostDto dto = new PostDto();
        dto.setUserName(post.getUserName());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setStatus("모집 중");
        dto.setVisitDate(post.getVisitDate());
        dto.setPrefer(post.getPrefer());
        dto.setUserId(post.getUserId());
        dto.setRestaurantId(post.getRestaurantId());
        return dto;
    }
}
