package com.example.final_project_17team.post.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreatePostDto {
    private String title;
    private String content;
//    private LocalDateTime visitDate;
    private String visitDate;
    private String prefer;
}
