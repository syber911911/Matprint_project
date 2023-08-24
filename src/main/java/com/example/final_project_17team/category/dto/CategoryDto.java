package com.example.final_project_17team.category.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto {
    private Long id;
    private String category;
    private String ref_url;
    private String content;
    private String restaurant_id;
}
