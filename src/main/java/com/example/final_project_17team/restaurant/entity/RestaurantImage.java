package com.example.final_project_17team.restaurant.entity;

import com.example.final_project_17team.global.entity.Base;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantImage extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Restaurant restaurant;

    @Builder
    public RestaurantImage(String url, Restaurant restaurant) {
        this.url = url;
        this.restaurant = restaurant;
    }
}
