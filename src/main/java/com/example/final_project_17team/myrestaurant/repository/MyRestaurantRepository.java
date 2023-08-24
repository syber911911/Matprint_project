package com.example.final_project_17team.myrestaurant.repository;

import com.example.final_project_17team.myrestaurant.entity.MyRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MyRestaurantRepository extends JpaRepository<MyRestaurant, Long> {

}
