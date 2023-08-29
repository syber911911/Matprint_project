package com.example.final_project_17team.user.entity;

import com.example.final_project_17team.comment.entity.Comment;
import com.example.final_project_17team.global.entity.Base;
import com.example.final_project_17team.post.entity.Post;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private boolean gender;
    @Column(nullable = false)
    private Long age;
    private String img_url;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Post> posts = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Comment> comments = new ArrayList<>();

}
