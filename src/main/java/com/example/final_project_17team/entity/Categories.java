package com.example.final_project_17team.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Table
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private String ref_url; // youtube 등 url
    private String content; // 연예인, 인플루언서 등 소개

    @ManyToMany
    @JoinColumn(name = "restaurant_id")
    private List<Restaurant> restaurants = new ArrayList<>();
}
