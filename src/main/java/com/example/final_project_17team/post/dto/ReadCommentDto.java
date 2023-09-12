package com.example.final_project_17team.post.dto;

import com.example.final_project_17team.post.entity.Comment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReadCommentDto {
    private Long id;
    private String content;
    private String username;

    public static ReadCommentDto fromEntity(Comment comment){
        ReadCommentDto commentDto = new ReadCommentDto();
        commentDto.setId(comment.getId());
        commentDto.setUsername(comment.getUser().getUsername());
        commentDto.setContent(comment.getContent());
        return commentDto;
    }

    @Data
    public static class CommentWithUser {
        private String accessUser;
        private Page<ReadCommentDto> comment;
    }
}
