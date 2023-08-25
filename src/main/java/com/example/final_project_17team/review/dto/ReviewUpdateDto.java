package com.example.final_project_17team.review.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ReviewUpdateDto {
    private String title;
    private String content;
    private Long ratings;
    private List<MultipartFile> updateImageList;
    private List<String> imageUrl;

}