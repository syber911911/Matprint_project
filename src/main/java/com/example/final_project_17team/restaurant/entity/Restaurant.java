package com.example.final_project_17team.restaurant.entity;

import com.example.final_project_17team.category.entity.Category;
import com.example.final_project_17team.global.entity.Base;
import com.example.final_project_17team.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "restaurants")
public class Restaurant extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;
    private String name;
    private String tel;
    private String openHours;
    private String closeHours;
    private String location;
    private String address;
    private String roadAddress;
    @Column(length = 1000)
    private String menuInfo;

    @Column(name = "map_x", precision = 20, scale = 17)
    private BigDecimal mapX;
    @Column(name = "map_y", precision = 20, scale = 18)
    private BigDecimal mapY;

    @Formula("(select avg(reviews.ratings) from reviews where reviews.restaurant_id = id)")
    private Float avgRatings;

    @Basic(fetch = FetchType.LAZY)
    @Formula("(select count(*) from reviews where reviews.restaurant_id = id)")
    private Integer reviewCount;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "restaurant_id")
    private List<RestaurantImage> restaurantImageList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "restaurant_id")
    private List<Category> categoryList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "restaurant_id")
    private List<Review> reviewList = new ArrayList<>();

    @Builder
    public Restaurant(
            String status, String name,
            String tel, String openHours,
            String closeHours, String location,
            String address, String roadAddress,
            String menuInfo, BigDecimal mapX,
            BigDecimal mapY
    ) {
        this.status = status;
        this.name = name;
        this.tel = tel;
        this.openHours = openHours;
        this.closeHours = closeHours;
        this.location = location;
        this.address = address;
        this.roadAddress = roadAddress;
        this.menuInfo = menuInfo;
        this.mapX = mapX;
        this.mapY = mapY;
    }
}
