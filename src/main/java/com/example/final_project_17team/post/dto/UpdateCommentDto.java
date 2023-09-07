package com.example.final_project_17team.post.dto;

import lombok.Data;

@Data
public class UpdateCommentDto {
    private String content;

    public boolean contentIsNotModified(String originContent) {
        return originContent.equals(this.content);
    }
}
