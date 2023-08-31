package com.example.final_project_17team.reviewImages.entity;

import com.example.final_project_17team.global.entity.Base;
import com.example.final_project_17team.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImages extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(cascade = CascadeType.PERSIST) // 수정 업뎃 하면 one 쪽 엔티티에서도 반영되게
    @ToString.Exclude
    private Review review;

    @Builder
    public ReviewImages(Long id, String imageUrl, Review review) {
        this.imageUrl = imageUrl;
        this.review = review;
    }
}
