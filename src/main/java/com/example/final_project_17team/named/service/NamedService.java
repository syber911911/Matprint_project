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

        Pageable pageable = PageRequest.of(
                pageNumber, pageSize, Sort.by("id").ascending());

        Page<Restaurant> NamedPage
                = restaurantRepository.findAllByCategoryList_Name(category, pageable);

        Page<NamedPageDto> NamedDtoPage
                = NamedPage.map(NamedPageDto::fromEntity);

        return NamedDtoPage;
    }

    public Page<NamedPageDto> readSortPage(String category, String sortBy, Integer pageNumber, Integer pageSize) {

        if(sortBy.equals("이름순")) {
            Pageable pageable = PageRequest.of(
                    pageNumber, pageSize, Sort.by("name").ascending());

            Page<Restaurant> NamedPage
                    = restaurantRepository.findAllByCategoryList_Name(category, pageable);

            Page<NamedPageDto> NamedDtoPage
                    = NamedPage.map(NamedPageDto::fromEntity);

            return NamedDtoPage;
        }
        /*   나중에 추가할 것들. 상세 페이지에 리뷰 카운트를 불러와서 정렬
        if(sortBy.equals("리뷰많은순")) {
            Pageable pageable = PageRequest.of(
                    pageNumber, pageSize, Sort.by("").ascending());
        }
        */
        /*  나중에 추가할 것들. 현재 내 위치와 좌표 비교하는 거리 알고리즘 이용해서 오름차순으로 정렬하면 될거 같음
        if(sortBy.equals("가까운순")) {
            Pageable pageable = PageRequest.of(
                    pageNumber, pageSize, Sort.by("").ascending());
        }
        */
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);

    }
}
