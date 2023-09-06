package com.example.final_project_17team.post.dto;

import com.example.final_project_17team.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {
    private String title;
    private String content;
    private String status;
    private LocalDateTime visitDate;
    private String prefer;
    private String username;
    private Integer age;
    private String gender;

    public static PostDto fromEntity(Post post){
        PostDto dto = new PostDto();
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setStatus(post.getStatus());
        dto.setVisitDate(post.getVisitDate());
        dto.setPrefer(post.getPrefer());
        dto.setUsername(post.getUser().getUsername());
        dto.setAge(post.getUser().getAge());
        dto.setGender(post.getUser().getGender());
        return dto;
    }

    @Data
    public static class PostWithUser {
        private String accessUser;
        private PostDto postDto;
    }
}
