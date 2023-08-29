package com.example.final_project_17team.comment.dto;

import com.example.final_project_17team.comment.entity.Comment;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {
    private Long id;
    private String content;
    private String username;
    private Long userId;
    private Long postId;

    public static CommentDto fromEntity(Comment comment){
        CommentDto dto = new CommentDto();
        dto.setUsername(comment.getUserName());
        dto.setContent(comment.getContent());
        return dto;
    }
}
