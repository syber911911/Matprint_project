package com.example.final_project_17team.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String status;
    private LocalDateTime visit_date;
    private String prefer;
    private LocalDateTime created_at;
    private LocalDateTime deleted_at;
    private LocalDateTime modified_at;
    private Long user_id;
    private Long restaurant_id;
}
