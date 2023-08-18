package com.example.final_project_17team.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Table
@ToString(exclude = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
