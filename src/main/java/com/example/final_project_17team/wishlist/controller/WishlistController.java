package com.example.final_project_17team.wishlist.controller;

import com.example.final_project_17team.wishlist.dto.WishlistDto;
import com.example.final_project_17team.wishlist.service.WishlistService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("/{restaurantId}")
    public Boolean setWish(@AuthenticationPrincipal String username, @PathVariable("restaurantId") Long restaurantId) {
        return wishlistService.setMyWish(username, restaurantId);
    }

    @GetMapping("/{restaurantId}")
    public Boolean isMyWish(@AuthenticationPrincipal String username, @PathVariable("restaurantId") Long restaurantId) {
        return wishlistService.isMyWish(username, restaurantId);
    }
}
