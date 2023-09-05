package com.example.final_project_17team.named.controller;

import com.example.final_project_17team.named.dto.NamedPageDto;
import com.example.final_project_17team.named.service.NamedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
//    @GetMapping
//    public Page<NamedPageDto> read(
//            @RequestParam("category") String category,
//            @RequestParam(defaultValue = "0") Integer page,
//            @RequestParam(defaultValue = "50") Integer limit
//    ){
//        return namedService.readNamedPage(category, page, limit);
//    }

    @GetMapping
    public Page<NamedPageDto> readSort(
            @RequestParam("category") String category,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ){
        return namedService.readNamed(category, sortBy, page, limit);
    }
}
