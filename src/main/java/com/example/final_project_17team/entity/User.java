package com.example.final_project_17team.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@Entity
@ToString(exclude = {"users"})
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
}
