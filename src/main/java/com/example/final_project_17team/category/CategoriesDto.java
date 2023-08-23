package com.example.final_project_17team.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoriesDto {
    private Long id;
    private String category;
    private String ref_url;
    private String content;
    private String restaurant_id;
}
