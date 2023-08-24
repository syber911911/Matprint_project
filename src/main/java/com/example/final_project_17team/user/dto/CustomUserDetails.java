package com.example.final_project_17team.user.dto;

import com.example.final_project_17team.user.entity.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;
    private String email;
    private String phone;
    private boolean gender; // 남성 : true, 여성 : false
    private Long age;
    private String imgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CustomUserDetails fromEntity(User user) {
        return CustomUserDetails.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.isGender())
                .age(user.getAge())
                .imgUrl(user.getImgUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdateAt())
                .build();
    }

    public static CustomUserDetails fromDto(JoinDto joinUser) {
        return CustomUserDetails.builder()
                .username(joinUser.getUsername())
                .username(joinUser.getUsername())
                .password(joinUser.getPassword())
                .email(joinUser.getEmail())
                .phone(joinUser.getPhone())
                .gender(joinUser.isGender())
                .age(joinUser.getAge())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
