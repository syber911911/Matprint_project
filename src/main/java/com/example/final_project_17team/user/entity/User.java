package com.example.final_project_17team.user.entity;

import com.example.final_project_17team.user.dto.CustomUserDetails;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String phone;
    @Column(nullable = false)
    private boolean gender;
    @Column(nullable = false)
    private Integer age;
    @Column(name = "img_url")
    private String imgUrl;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    @Builder
    public User(String username, String password, String email, String phone, boolean gender, Integer age, String imgUrl) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.age = age;
        this.imgUrl = imgUrl;
    }

    public static User fromUserDetails(CustomUserDetails userDetails) {
        return User.builder()
                .username(userDetails.getUsername())
                .password(userDetails.getPassword())
                .email(userDetails.getEmail())
                .phone(userDetails.getPhone())
                .gender(userDetails.isGender())
                .age(userDetails.getAge())
                .build();
    }
}
