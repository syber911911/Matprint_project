package com.example.final_project_17team.user.entity;

import com.example.final_project_17team.post.entity.Post;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
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
    private LocalDateTime created_at;
    private LocalDateTime modified_at;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Post> posts = new ArrayList<>();

}
