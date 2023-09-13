package com.example.final_project_17team.post.dto;

import com.example.final_project_17team.post.entity.Comment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {
    private Long id;
    private String content;
    private String username;
    private String imgUrl;

    public static CommentDto fromEntity(Comment comment){
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setUsername(comment.getUser().getUsername());
        commentDto.setContent(comment.getContent());
        commentDto.setImgUrl(comment.getUser().getImgUrl());
        return commentDto;
    }

    @Data
    public static class CommentWithUser {
        private String accessUser;
        private Page<CommentDto> comment;
    }
}