package com.example.final_project_17team.review.dto;

import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.reviewImages.entity.ReviewImages;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateReviewDto {
    @NotBlank(message = "리뷰 내용을 작성해주세요.")
    private String content;
    private Float ratings;
    private List<MultipartFile> imageList;
}
