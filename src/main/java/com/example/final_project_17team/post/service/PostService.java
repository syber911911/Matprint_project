package com.example.final_project_17team.post.service;

import com.example.final_project_17team.comment.dto.CommentDto;
import com.example.final_project_17team.comment.entity.Comment;
import com.example.final_project_17team.comment.repository.CommentRepository;
import com.example.final_project_17team.post.dto.PostDto;
import com.example.final_project_17team.post.entity.Post;
import com.example.final_project_17team.post.repository.PostRepository;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 동행 음식점 미지정인 경우 : restaurantId = 0
    public PostDto createPost(PostDto dto, Long restaurantId){
        User user = getUserByAuth();
        if (restaurantRepository.findById(restaurantId).isEmpty() && restaurantId != 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setStatus("모집 중");
        post.setVisitDate(dto.getVisitDate());
        post.setPrefer(dto.getPrefer());
        post.setUserId(user.getId());
        if (restaurantId != 0) post.setRestaurantId(restaurantId);

        postRepository.save(post);
        return PostDto.fromEntity(post);
    }

    // 수정은 [제목, 내용, 방문날짜, 선호, Status]에 한해서 됨
    public PostDto updatePost(PostDto dto, Long postId){
        User user = getUserByAuth();
        Post post = getPostById(postId, user.getId());

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setPrefer(dto.getPrefer());
        post.setVisitDate(dto.getVisitDate());

        if (dto.getStatus().equals("모집 중") || dto.getStatus().equals("모집 완료"))
            post.setStatus(dto.getStatus());

        postRepository.save(post);
        return PostDto.fromEntity(post);
    }

    public void deletePost(Long postId){
        User user = getUserByAuth();
        Post post = getPostById(postId, user.getId());

        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    public Page<PostDto> searchPost(String targets, Integer pageNumber, Integer pageSize) {
        List<String> targetList = Arrays.asList(targets.split(" "));
        List<Post> postList = postRepository.findAll();
        List<PostDto> postDtoList = new ArrayList<>();

        for (Post post : postList){
            int cnt = 0;
            for (String target : targetList) {
                if (post.getTitle().contains(target) || post.getContent().contains(target)) {
                    if (++cnt == targetList.size()) {
                        postDtoList.add(PostDto.fromEntity(post));
                    }
                } else break;
            }
        }
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), postDtoList.size());
        return new PageImpl<>(postDtoList.subList(start, end), pageRequest, postDtoList.size());
    }

    public CommentDto crateComment(CommentDto dto, Long postId) {
        User user = getUserByAuth();

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setUserId(user.getId());
        comment.setUserName(user.getUsername());
        comment.setPostId(postId);
        commentRepository.save(comment);

        return CommentDto.fromEntity(comment);
    }

    //TODO 동행 조회(전체 조회와 제목, 내용에 키워드 포함 조회는 구현 했는데 gender, age, status 등으로 조회하는건 아직 구현 안함)
    //TODO 댓글 조회(동행 글 작성자와, 댓글 작성자 들만 볼 수 있게), 수정, 삭제

    public Page<CommentDto> readComment(Long postId, Integer pageNumber, Integer pageSize) {
        User user = getUserByAuth();
        Post post = getPostById(postId, user.getId());

        Pageable pageable = PageRequest.of(
                pageNumber, pageSize, Sort.by("createdAt").ascending()); // 오름차순 정렬

        return commentRepository.findAllByPostId(post.getId(), pageable)
                .map(CommentDto::fromEntity);
    }

    public CommentDto updateComment(CommentDto dto, Long commentId) {
        User user = getUserByAuth();
        Comment comment = getCommentById(commentId, user.getId());
        comment.setContent(dto.getContent());
        commentRepository.save(comment);
        return CommentDto.fromEntity(comment);
    }

    public void deleteComment(Long commentId) {
        User user = getUserByAuth();
        Comment comment = getCommentById(commentId, user.getId());

        comment.setDeletedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public User getUserByAuth() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return optionalUser.get();
    }

    public Post getPostById(Long postId, Long userId) {
        Optional<Post> optionalPost = postRepository.findByIdAndUserId(postId, userId);
        if(optionalPost.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return optionalPost.get();
    }

    public Comment getCommentById(Long commentId, Long userId) {
        Optional<Comment> optionalComment = commentRepository.findByIdAndUserId(commentId, userId);
        if (optionalComment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optionalComment.get();
    }
}
