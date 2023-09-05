package com.example.final_project_17team.review.dto;

import com.example.final_project_17team.review.entity.Review;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateReviewDto {
    @NotBlank(message = "리뷰 내용을 작성해주세요.")
    private String content;
    @NotNull(message = "평점을 선택해주세요.")
    private Integer ratings;
    private List<MultipartFile> addImageList;
    private List<String> deleteImageList;

    public boolean contentIsNotModified(String originContent) {
        return originContent.equals(this.content);
    }

    public boolean ratingsIsNotModified(Integer originRatings) {
        return originRatings.equals(this.ratings);
    }

    public boolean addImageIsNull() {
        return CollectionUtils.isEmpty(this.addImageList) || this.addImageList.get(0).isEmpty();
    }

    public boolean deleteImageIsNull() {
        return CollectionUtils.isEmpty(deleteImageList);
    }

    public boolean isNotModified(Review review) {
        return addImageIsNull() && deleteImageIsNull() && contentIsNotModified(review.getContent()) && ratingsIsNotModified(review.getRatings());
    }
}