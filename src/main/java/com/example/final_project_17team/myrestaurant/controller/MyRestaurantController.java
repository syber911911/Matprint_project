package com.example.final_project_17team.myrestaurant.controller;

import com.example.final_project_17team.myrestaurant.dto.MyRestaurantDto;
import com.example.final_project_17team.myrestaurant.service.MyRestaurantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class MyRestaurantController {
    private final MyRestaurantService service;

    @GetMapping("/myRestaurant")
    public ResponseEntity<List<MyRestaurantDto>> getMyRestaurant(@RequestParam("myRestaurantId") Long myRestaurantId) {
        List<MyRestaurantDto> wishlist = service.myRestaurantView(myRestaurantId);
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/visited")
    public ResponseEntity<String> Visited(@RequestParam("myRestaurantId") Long myRestaurantId) {
        service.setVisited(myRestaurantId);
        return ResponseEntity.ok("visited");
    }

    @DeleteMapping("/deleteMyRestaurant")
    public void deleteMyRestaurant(@RequestParam("myRestaurantId") Long myRestaurantId){
        service.deleteMyRestaurant(myRestaurantId);
    }


}
