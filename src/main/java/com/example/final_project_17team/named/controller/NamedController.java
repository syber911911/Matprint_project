package com.example.final_project_17team.named.controller;

import com.example.final_project_17team.named.dto.NamedPageDto;
import com.example.final_project_17team.named.service.NamedService;
import com.example.final_project_17team.restaurant.dto.RestaurantSearchDto;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/named")
public class NamedController {

    private final NamedService namedService;

    // 인플루언서 맛집 찾기/카테고리 선택 조회
    @GetMapping("")
    public Page<NamedPageDto> read(
            @RequestParam("category") String category,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer limit
    ){
        return namedService.readNamedPage(category, page, limit);
    }

    // 이름순, 리뷰순 등을 누르면 정렬되게
    @GetMapping("/sort")
    public Page<NamedPageDto> readSort(
            @RequestParam("category") String category,
            @RequestParam("sortBy") String sortBy,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer limit
    ){
        return namedService.readSortPage(category, sortBy, page, limit);
    }

}
