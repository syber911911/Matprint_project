package com.example.final_project_17team.post;

import com.example.final_project_17team.restaurant.Restaurant;
import com.example.final_project_17team.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private String status;
    private LocalDateTime visit_date;
    private String prefer;
    private LocalDateTime created_at;
    private LocalDateTime deleted_at;
    private LocalDateTime modified_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
