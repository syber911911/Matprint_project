package com.example.final_project_17team.review.entity;

import com.example.final_project_17team.global.entity.Base;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.reviewImages.entity.ReviewImages;
import com.example.final_project_17team.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "reviews")
@Where(clause = "deleted = false")
public class Review extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Long ratings;
    private String img_url;
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "review_id")
    private List<ReviewImages> reviewImages = new ArrayList<>();
}