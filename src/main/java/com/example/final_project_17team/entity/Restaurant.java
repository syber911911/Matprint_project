package com.example.final_project_17team.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private float lat;
    @Column(nullable = false)
    private float lon;
    private Long phone;
    private float avg_ratings;
    private String runtime;
    private String menu_info;
    private String img_url;
    private LocalDateTime created_at;
    private LocalDateTime deleted_at;
    private LocalDateTime modified_at;
}
