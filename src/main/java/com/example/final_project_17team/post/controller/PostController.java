package com.example.final_project_17team.post.controller;

import com.example.final_project_17team.global.dto.ResponseDto;
import com.example.final_project_17team.post.dto.PostDto;
import com.example.final_project_17team.post.dto.UpdatePostDto;
import com.example.final_project_17team.post.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/mate")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseDto create(@RequestBody PostDto request, @AuthenticationPrincipal String username){
        return postService.createPost(request, username);
    }

    @GetMapping
    public Page<PostDto> readAll(
            @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "limit", defaultValue = "5") Integer pageSize
    ) {
        return postService.readAllPost(pageNumber, pageSize);
    }

    @GetMapping("/{postId}")
    public PostDto.PostWithUser read(@PathVariable("postId") Long postId, @AuthenticationPrincipal String username) {
        return postService.readPost(postId, username);
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
    public Page<PostDto> search(
            @RequestParam(name = "type", defaultValue = "제목") String type,
            @RequestParam(name="target", defaultValue="") String keyword,
            @RequestParam(name="gender", defaultValue = "") String gender,
            @RequestParam(name="age", defaultValue = "0") Integer age,
            @RequestParam(name="status", defaultValue = "") String status,
            @RequestParam(defaultValue="0") Integer page,
            @RequestParam(defaultValue="5") Integer limit
    ){
        return postService.searchPost(type, keyword, gender, age, status, page, limit);
    }

//    @PostMapping("/{postId}/comment")
//    public ResponseEntity<Map<String, String>> createComment(
//            @RequestBody CommentDto dto,
//            @PathVariable("postId") Long postId
//    ) {
//        postService.crateComment(dto, postId);
//        log.info(dto.toString());
//        return setResponseEntity("댓글이 등록되었습니다.");
//    }
//
//    @GetMapping("/{postId}/comment")
//    public Page<CommentDto> readAllComment(
//            @PathVariable("postId") Long postId,
//            @RequestParam(defaultValue = "0") Integer page,
//            @RequestParam(defaultValue = "10") Integer limit
//    ) {
//        return postService.readComment(postId,page,limit);
//    }
//
//    @PutMapping("/{postId}/comment/{commentId}")
//    public ResponseEntity<Map<String, String>> updateComment(
//            @PathVariable("postId") Long postId,
//            @PathVariable("commentId") Long commentId,
//            @RequestBody CommentDto dto
//    ) {
//        postService.updateComment(dto, commentId);
//        log.info(dto.toString());
//        return setResponseEntity("댓글이 수정되었습니다.");
//    }
//
//    @DeleteMapping("/{postId}/comment/{commentId}")
//    public ResponseEntity<Map<String, String>> deleteComment(
//            @PathVariable("postId") Long postId,
//            @PathVariable("commentId") Long commentId
//    ) {
//        postService.deleteComment(commentId);
//        return setResponseEntity("댓글이 삭제되었습니다.");
//    }
//
//    public ResponseEntity<Map<String, String>> setResponseEntity(String message) {
//        Map<String, String> responseBody = new HashMap<>();
//        responseBody.put("message", message);
//        return ResponseEntity.ok(responseBody);
//    }
}
