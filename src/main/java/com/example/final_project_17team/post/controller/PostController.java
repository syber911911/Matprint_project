package com.example.final_project_17team.post.controller;

import com.example.final_project_17team.post.dto.PostDto;
import com.example.final_project_17team.post.service.PostService;
import com.example.final_project_17team.review.dto.ReviewRequestDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/mate")
public class PostController {

    private final PostService postService;

    @PostMapping("")
    public ResponseEntity<Map<String, String>> create(
            @RequestBody PostDto dto,
            @RequestParam("restaurantId") Long restaurantId
    ){

        postService.createPost(dto, restaurantId);

        log.info(dto.toString());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "동행 찾기 등록이 완료되었습니다.");

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/post/{postId}")
    public ResponseEntity<Map<String, String>> update(
            @RequestBody PostDto dto,
            @PathVariable("postId") Long postId
    ){

        postService.updatePost(dto, postId);

        log.info(dto.toString());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "동행 찾기 등록이 수정되었습니다.");

        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable("postId") Long postId
    ) {
        if (postService.deletePost(postId)) {

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "동행 모집 글을 삭제했습니다.");

            return ResponseEntity.ok(responseBody);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}
