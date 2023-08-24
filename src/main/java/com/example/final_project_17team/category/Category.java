package com.example.final_project_17team.category;

import com.example.final_project_17team.global.entity.Base;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String refUrl;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Restaurant restaurant;

    @Builder
    public Category(String name, String refUrl, Restaurant restaurant) {
        this.name = name;
        this.refUrl = refUrl;
        this.restaurant = restaurant;
    }
}
