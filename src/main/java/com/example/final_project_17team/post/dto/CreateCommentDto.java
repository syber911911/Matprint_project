package com.example.final_project_17team.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentDto {
    @NotBlank(message = "댓글 내용을 작성해주세요.")
    private String content;
}
