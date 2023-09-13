package com.example.final_project_17team.restaurant.service;

import com.example.final_project_17team.global.dto.PlaceDataDto;
import com.example.final_project_17team.global.service.CategoryUpdate;
import com.example.final_project_17team.restaurant.dto.RestaurantDetailDto;
import com.example.final_project_17team.restaurant.dto.RestaurantDto;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;


@Slf4j
@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final CategoryUpdate categoryUpdate;

    // 사용자의 검색어를 기준으로
    // naver maps api 호출 후
    // 결과를 page 로 반환
    public Page<RestaurantDto> searchRestaurant(String target, Integer pageNumber, Integer pageSize) {
        List<PlaceDataDto> placeDataDtoList = categoryUpdate.getPlaceList(target + " " + "음식점", 100);
        if (placeDataDtoList == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 검색어의 결과가 없음");

        List<RestaurantDto> restaurantDtoList = new ArrayList<>();
        for (PlaceDataDto place : placeDataDtoList) {
            restaurantDtoList.add(RestaurantDto.fromDto(place));
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), restaurantDtoList.size());
        try {
            return new PageImpl<>(restaurantDtoList.subList(start, end), pageable, restaurantDtoList.size());
        } catch (IllegalArgumentException ex) {
            return new PageImpl<>(restaurantDtoList.subList(restaurantDtoList.size(), restaurantDtoList.size()), pageable, restaurantDtoList.size());
        }
    }

    // 사용자가 음식점 리스트 중 하나를 선택
    // 선택된 음식점의 이름, x좌표, y좌표 기준으로
    // DB 조회
    // 조회된 결과가 없다면 해당 음식점의 name 과 address 로 api 호출 후 DB 저장
    // 조회된 결과가 있다면 update 일자를 확인하고 update 일자가 30일 이상이라면 api 호출 결과로 update 후 반환

    public RestaurantDetailDto detailPage(String name, String address) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findByNameAndAddress(name, address);

        if (optionalRestaurant.isEmpty()) {
            address = address.split("\s[0-9]")[0];
            List<PlaceDataDto> placeDataDtoList = categoryUpdate.getPlaceList(name + " " + address, 1);
            if (placeDataDtoList == null) {
                log.warn("api search query : {}", address + " " + name);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류");
            }
            PlaceDataDto placeDataDto = placeDataDtoList.get(0);
            Restaurant restaurant = categoryUpdate.saveRestaurant(placeDataDto);
            return RestaurantDetailDto.fromEntity(restaurant);
        } else {
            Restaurant restaurant = optionalRestaurant.get();
            // 조회된 결과가 있다면 update 일자를 확인하고 update 일자가 30일 이상이라면 api 호출 결과로 update 후 반환
            return RestaurantDetailDto.fromEntity(restaurant);
        }
    }

    public RestaurantDetailDto readRestaurantWithId(Long id) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(id);
        if (optionalRestaurant.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "음식점 아이디가 유효하지 않습니다.");
        return RestaurantDetailDto.fromEntity(optionalRestaurant.get());
    }
}
