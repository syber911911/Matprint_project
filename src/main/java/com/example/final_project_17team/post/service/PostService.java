package com.example.final_project_17team.post.service;

import com.example.final_project_17team.comment.entity.Comment;
import com.example.final_project_17team.comment.repository.CommentRepository;
import com.example.final_project_17team.global.dto.ResponseDto;
import com.example.final_project_17team.post.dto.PostDto;
import com.example.final_project_17team.post.dto.UpdatePostDto;
import com.example.final_project_17team.post.entity.Post;
import com.example.final_project_17team.post.repository.PostRepository;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public ResponseDto createPost(PostDto request, String username) {
        User user = this.getUser(username);
        postRepository.save(Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .status("모집 중")
                .visitDate(request.getVisitDate())
                .prefer(request.getPrefer())
                .user(user)
                .build());

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("동행 글 등록이 완료되었습니다.");
        responseDto.setStatus(HttpStatus.OK);
        return responseDto;
    }

    public Page<PostDto> readAllPost(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(PostDto::fromEntity);
    }

    public PostDto.PostWithUser readPost(Long postId, String username) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 동행 글을 찾을 수 없습니다.");
        Post post = optionalPost.get();

        PostDto postDto = PostDto.fromEntity(post);
        PostDto.PostWithUser postWithUser = new PostDto.PostWithUser();
        postWithUser.setAccessUser(username);
        postWithUser.setPostDto(postDto);

        return postWithUser;
    }

    public ResponseDto updatePost(UpdatePostDto request, Long postId, String username) {
        User user = getUser(username);
        Post post = getPost(postId, user);

        if (request.isNotModified(post))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "동행 글의 수정사항이 없습니다.");

        post.updatePost(request);
        postRepository.save(post);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("동행 글 수정이 완료되었습니다.");
        responseDto.setStatus(HttpStatus.OK);
        return responseDto;
    }

    public ResponseDto deletePost(Long postId, String username) {
        User user = this.getUser(username);
        Post post = this.getPost(postId, user);
        postRepository.delete(post);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("동행 글 삭제가 완료되었습니다.");
        responseDto.setStatus(HttpStatus.OK);
        return responseDto;
    }

    // 검색어를 통한 글 검색
    public Page<PostDto> searchPost(String type, String keyword, String gender, Integer age, String status, Integer pageNumber, Integer pageSize) {
        Specification<Post> spec = (root, query, criteriaBuilder) -> null;
        if (keyword.isBlank() || keyword.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요.");

        switch (type) {
            case "제목" -> spec = spec.and(PostSpecification.containsTitle(keyword));
            case "내용" -> spec = spec.and(PostSpecification.containsContent(keyword));
            case "작성자" -> spec = spec.and(PostSpecification.equalUsername(keyword));
        }

        if (!gender.isEmpty())
            spec = spec.and(PostSpecification.equalGender(gender));

        if (!age.equals(0))
            spec = spec.and(PostSpecification.inBoundAge(age));

        if (!status.isEmpty())
            spec = spec.and(PostSpecification.equalStatus(status));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        Page<Post> searchResult = postRepository.findAll(spec, pageable);
        return searchResult.map(PostDto::fromEntity);
    }

//    public CommentDto crateComment(CommentDto dto, Long postId, String username) {
//        User user = getUser(username);
//        Post post = getPost(postId, user);
//
//        Comment comment = new Comment();
//        comment.setContent(dto.getContent());
//        comment.setUserId(user.getId());
//        comment.setPostId(post.getId());
//
//        commentRepository.save(comment);
//        return CommentDto.fromEntity(comment);
//    }
//
//    public Page<CommentDto> readComment(Long postId, Integer pageNumber, Integer pageSize) {
//        User user = loadUserByAuth();
//        Post post = loadPostById(postId, user.getId());
//
//        Pageable pageable = PageRequest.of(
//                pageNumber, pageSize, Sort.by("createdAt").ascending()); // 오름차순 정렬
//
//        return commentRepository.findAllByPostId(post.getId(), pageable)
//                .map(CommentDto::fromEntity);
//    }
//
//    public CommentDto updateComment(CommentDto dto, Long commentId) {
//        User user = loadUserByAuth();
//        Comment comment = loadCommentById(commentId, user.getId());
//        comment.setContent(dto.getContent());
//        commentRepository.save(comment);
//        return CommentDto.fromEntity(comment);
//    }
//
//    public void deleteComment(Long commentId) {
//        User user = loadUserByAuth();
//        Comment comment = loadCommentById(commentId, user.getId());
//
//        comment.setDeletedAt(LocalDateTime.now());
//        commentRepository.save(comment);
//    }

    public User getUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자를 찾을 수 없습니다.");
        return optionalUser.get();
    }

    public User loadUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        return optionalUser.get();
    }

    public Post getPost(Long postId, User user) {
        Optional<Post> optionalPost = postRepository.findByIdAndUser(postId, user);
        if (optionalPost.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found");
        return optionalPost.get();
    }

    public Comment loadCommentById(Long commentId, Long userId) {
        Optional<Comment> optionalComment = commentRepository.findByIdAndUserId(commentId, userId);
        if (optionalComment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optionalComment.get();
    }
}
