package com.example.final_project_17team.post.dto;

import com.example.final_project_17team.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdatePostDto {
    @NotBlank(message = "제목을 작성해주세요.")
    private String title;
    @NotBlank(message = "내용을 작성해주세요.")
    private String content;
    @NotBlank(message = "모집 상태를 선택해주세요.")
    @Pattern(regexp = "모집 중|모집 완료")
    private String status;
//    private LocalDateTime visitDate;
    private String visitDate;

    public boolean titleIsNotModified(String originTitle) {
        return originTitle.equals(this.title);
    }

    public boolean contentIsNotModified(String originContent) {
        return originContent.equals(this.content);
    }

    public boolean statusIsNotModified(String originStatus) {
        return originStatus.equals(this.status);
    }

    public boolean visitDataIsNotModified(String originVisitData) {
        return originVisitData.equals(this.visitDate);
    }

    public boolean isNotModified(Post post) {
        return titleIsNotModified(post.getTitle()) && contentIsNotModified(post.getContent()) && statusIsNotModified(post.getStatus()) && visitDataIsNotModified(post.getVisitDate());
    }
}
