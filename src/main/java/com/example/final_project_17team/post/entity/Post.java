package com.example.final_project_17team.post.entity;

import com.example.final_project_17team.comment.entity.Comment;
import com.example.final_project_17team.global.entity.Base;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "posts")
@Where(clause = "deleted = false")
public class Post extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    private String status;
    private LocalDateTime visitDate;
    private String prefer;
    private boolean deleted;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @OneToMany
    @JoinColumn(name = "post_id")
    private List<Comment> comments = new ArrayList<>();
}
