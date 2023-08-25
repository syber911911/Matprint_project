package com.example.final_project_17team.reviewImages.entity;

import com.example.final_project_17team.global.entity.Base;
import com.example.final_project_17team.review.entity.Review;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Data
public class ReviewImages extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image_url;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // 수정 업뎃 하면 one 쪽 엔티티에서도 반영되게
    @ToString.Exclude
    private Review review;
}
