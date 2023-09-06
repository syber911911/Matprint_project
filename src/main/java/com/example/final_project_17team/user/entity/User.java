package com.example.final_project_17team.user.entity;

import com.example.final_project_17team.global.entity.Base;
import com.example.final_project_17team.user.dto.CustomUserDetails;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User extends Base {
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
    private String gender;
    @Column(nullable = false)
    private Integer age;
    @Column(name = "img_url")
    private String imgUrl;

    @Builder
    public User(String username, String password, String email, String phone, String gender, Integer age, String imgUrl) {
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
                .gender(userDetails.getGender())
                .age(userDetails.getAge())
                .build();
    }
}
