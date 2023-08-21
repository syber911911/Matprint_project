package com.example.final_project_17team.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private String ref_url; // youtube 등 url
    private String content; // 연예인, 인플루언서 등 소개

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private List<Restaurant> restaurants = new ArrayList<>();
}
