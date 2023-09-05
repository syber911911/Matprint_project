package com.example.final_project_17team.wishlist.controller;

import com.example.final_project_17team.wishlist.dto.WishlistDto;
import com.example.final_project_17team.wishlist.service.WishlistService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/wishlist")
public class WishlistControlle {
    private final WishlistService service;

//    @GetMapping
//    public ResponseEntity<List<WishlistDto>> getMyRestaurant(@RequestParam("myRestaurantId") Long myRestaurantId) {
//        List<WishlistDto> wishlist = service.myRestaurantView(myRestaurantId);
//        return ResponseEntity.ok(wishlist);
//    }
//
//    @PostMapping("/visited")
//    public ResponseEntity<String> Visited(@RequestParam("myRestaurantId") Long myRestaurantId) {
//        service.setVisited(myRestaurantId);
//        return ResponseEntity.ok("visited");
//    }
//
//    @DeleteMapping("/deleteMyRestaurant")
//    public void deleteMyRestaurant(@RequestParam("myRestaurantId") Long myRestaurantId){
//        service.deleteMyRestaurant(myRestaurantId);
//    }
}
