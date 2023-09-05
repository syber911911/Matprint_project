package com.example.final_project_17team.review.entity;

import com.example.final_project_17team.global.entity.Base;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.reviewImages.entity.ReviewImages;
import com.example.final_project_17team.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Integer ratings;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "review_id")
    private List<ReviewImages> reviewImages = new ArrayList<>();

    @Builder
    public Review(String content, Integer ratings, User user, Restaurant restaurant, List<ReviewImages> reviewImages) {
        this.content = content;
        this.ratings = ratings;
        this.user = user;
        this.restaurant = restaurant;
        this.reviewImages = reviewImages;
    }

    public void updateContentAndRatings(String content, Integer ratings) {
        this.content = content;
        this.ratings = ratings;
    }
}