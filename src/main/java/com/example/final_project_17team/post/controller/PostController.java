package com.example.final_project_17team.post.controller;

import com.example.final_project_17team.global.dto.ResponseDto;
import com.example.final_project_17team.post.dto.*;
import com.example.final_project_17team.post.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/mate")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseDto create(@RequestBody CreatePostDto request, @AuthenticationPrincipal String username) {
        return postService.createPost(request, username);
    }

    @GetMapping
    public Page<ReadPostDto> readAllPost(
            @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "limit", defaultValue = "5") Integer pageSize
    ) {
        return postService.readAllPost(pageNumber, pageSize);
    }

    @GetMapping("/{postId}")
    public PostDto.PostWithUser readPostDetail(@PathVariable("postId") Long postId, @AuthenticationPrincipal String username) {
        return postService.readPostDetail(postId, username);
    }

    @PutMapping("/{postId}")
    public ResponseDto update(@RequestBody UpdatePostDto request, @PathVariable("postId") Long postId, @AuthenticationPrincipal String username) {
        System.out.println(request.toString());
        return postService.updatePost(request, postId, username);
    }

    @DeleteMapping("/{postId}")
    public ResponseDto delete(@PathVariable("postId") Long postId, @AuthenticationPrincipal String username) {
        return postService.deletePost(postId, username);
    }

    @GetMapping("/search")
    public Page<ReadPostDto> search(
            @RequestParam(name = "type", defaultValue = "제목") String type,
            @RequestParam(name = "target", defaultValue = "") String keyword,
            @RequestParam(name = "gender", defaultValue = "") String gender,
            @RequestParam(name = "age", defaultValue = "0") Integer age,
            @RequestParam(name = "status", defaultValue = "") String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer limit
    ) {
        return postService.searchPost(type, keyword, gender, age, status, page, limit);
    }

    @PostMapping("/{postId}/comment")
    public ResponseDto createComment(@RequestBody CreateCommentDto request, @PathVariable("postId") Long postId, @AuthenticationPrincipal String username) {
        return postService.crateComment(request, postId, username);
    }

    @GetMapping("/{postId}/comment")
    public CommentDto.CommentWithUser readAllComment(
            @PathVariable("postId") Long postId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @AuthenticationPrincipal String username
    ) {
        return postService.readCommentPage(postId, page, limit, username);
    }

    @PutMapping("/{postId}/comment/{commentId}")
    public ResponseDto updateComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @RequestBody UpdateCommentDto request,
            @AuthenticationPrincipal String username
    ) {
        return postService.updateComment(request, postId, commentId, username);
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    public ResponseDto deleteComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal String username
    ) {
        return postService.deleteComment(postId, commentId, username);
    }
}
