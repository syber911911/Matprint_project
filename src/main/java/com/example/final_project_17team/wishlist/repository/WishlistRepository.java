package com.example.final_project_17team.wishlist.repository;

import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.wishlist.entity.Wishlist;
import com.example.final_project_17team.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByRestaurantIdAndUser(Long resId, User user);
    Optional<Wishlist> findByRestaurantAndUser(Restaurant restaurant, User user);
    List<Wishlist> findAllByUser(User user);
}
