package com.example.final_project_17team.myrestaurant.repository;

import com.example.final_project_17team.myrestaurant.entity.MyRestaurant;
import com.example.final_project_17team.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MyRestaurantRepository extends JpaRepository<MyRestaurant, Long> {
    boolean existsByRestaurantId(Long resId);

    Optional<MyRestaurant> findByRestaurantIdAndUser(Long resId, User user);

    Optional<MyRestaurant> findByUser(User user);
}
