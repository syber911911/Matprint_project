package com.example.final_project_17team.post.dto;

import com.example.final_project_17team.post.entity.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReadPostDto {
    private Long id;
    private String title;
    private String status;
    private LocalDateTime visitDate;
    private String username;

    public static ReadPostDto fromEntity(Post post){
        ReadPostDto readPostDto = new ReadPostDto();
        readPostDto.setId(post.getId());
        readPostDto.setTitle(post.getTitle());
        readPostDto.setStatus(post.getStatus());
        readPostDto.setVisitDate(post.getVisitDate());
        readPostDto.setUsername(post.getUser().getUsername());
        return readPostDto;
    }

    public static List<ReadPostDto> fromEntityList(List<Post> postList) {
        List<ReadPostDto> readPostDtoList = new ArrayList<>();
        if (postList.isEmpty())
            return null;

        for (Post post : postList) {
            readPostDtoList.add(fromEntity(post));
        }
        return readPostDtoList;
    }
}
