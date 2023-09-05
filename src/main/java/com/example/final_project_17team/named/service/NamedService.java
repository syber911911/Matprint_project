package com.example.final_project_17team.named.service;

import com.example.final_project_17team.category.entity.Category;
import com.example.final_project_17team.category.repository.CategoryRepository;
import com.example.final_project_17team.named.dto.NamedPageDto;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NamedService {

    private final RestaurantRepository restaurantRepository;

    public Page<NamedPageDto> readNamedPage(String category, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());
        Page<Restaurant> NamedPage = restaurantRepository.findAllByCategoryList_Name(category, pageable);
        return NamedPage.map(NamedPageDto::fromEntity);
    }

    public Page<NamedPageDto> readSortPage(String category, String sortBy, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                switch (sortBy) {
                    case "이름" -> Sort.by("name").ascending();
                    case "리뷰" -> Sort.by("reviewCount").descending();
                    case "평점" -> Sort.by("avgRatings").descending();
                    default -> Sort.by("id").ascending();
                }
        );
        Page<Restaurant> NamedPage = restaurantRepository.findAllByCategoryList_Name(category, pageable);
        return NamedPage.map(NamedPageDto::fromEntity);
    }
}
