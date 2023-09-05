package com.example.final_project_17team.post.entity;

import com.example.final_project_17team.comment.entity.Comment;
import com.example.final_project_17team.global.entity.Base;
import com.example.final_project_17team.post.dto.UpdatePostDto;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts")
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
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @OneToMany
    @JoinColumn(name = "post_id")
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(String title, String content, String status, LocalDateTime visitDate, String prefer, User user) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.visitDate = visitDate;
        this.prefer = prefer;
        this.user = user;
    }

    public void updatePost(UpdatePostDto request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.status = request.getStatus();
        this.visitDate = request.getVisitDate();
    }
}
