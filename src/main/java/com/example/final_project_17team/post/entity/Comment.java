package com.example.final_project_17team.post.entity;

import com.example.final_project_17team.global.entity.Base;
import com.example.final_project_17team.post.dto.UpdateCommentDto;
import com.example.final_project_17team.post.entity.Post;
import com.example.final_project_17team.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Builder
    public Comment(String content, User user, Post post) {
        this.content = content;
        this.user = user;
        this.post = post;
    }

    public void updateComment(UpdateCommentDto request) {
        this.content = request.getContent();
    }
}
