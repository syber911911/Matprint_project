package com.example.final_project_17team.post.service;

import com.example.final_project_17team.post.dto.*;
import com.example.final_project_17team.post.entity.Comment;
import com.example.final_project_17team.post.repository.CommentRepository;
import com.example.final_project_17team.global.dto.ResponseDto;
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

    public ResponseDto createPost(CreatePostDto request, String username) {
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

    public Page<ReadPostDto> readAllPost(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(ReadPostDto::fromEntity);
    }

    public PostDto.PostWithUser readPostDetail(Long postId, String username) {
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

    public List<ReadPostDto> readMyPost(String username) {
        User user = this.getUser(username);
        List<Post> postList = postRepository.findByUser(user);

        if (postList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사용자가 작성한 동행 글이 존재하지 않습니다.");

        return ReadPostDto.fromEntityList(postList);
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
    public Page<ReadPostDto> searchPost(String type, String keyword, String gender, Integer age, String status, Integer pageNumber, Integer pageSize) {
        Specification<Post> spec = (root, query, criteriaBuilder) -> null;
        if (!keyword.isBlank() || !keyword.isEmpty()) {
            switch (type) {
                case "제목" -> spec = spec.and(PostSpecification.containsTitle(keyword));
                case "내용" -> spec = spec.and(PostSpecification.containsContent(keyword));
                case "작성자" -> spec = spec.and(PostSpecification.equalUsername(keyword));
            }
        }

        if (!gender.isBlank())
            spec = spec.and(PostSpecification.equalGender(gender));

        if (!age.equals(0))
            spec = spec.and(PostSpecification.inBoundAge(age));

        if (!status.isBlank())
            spec = spec.and(PostSpecification.equalStatus(status));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        Page<Post> searchResult = postRepository.findAll(spec, pageable);
        return searchResult.map(ReadPostDto::fromEntity);
    }

    public ResponseDto crateComment(CreateCommentDto request, Long postId, String username) {
        User user = getUser(username);
        Post post = getPost(postId, user);

        commentRepository.save(
                Comment.builder()
                        .content(request.getContent())
                        .post(post)
                        .user(user)
                        .build()
        );
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("댓글 등록이 완료되었습니다.");
        responseDto.setStatus(HttpStatus.OK);
        return responseDto;
    }

    public CommentDto.CommentWithUser readCommentPage(Long postId, Integer pageNumber, Integer pageSize, String username) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 동행 글이 존재하지 않습니다.");
        Post post = optionalPost.get();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending()); // 내림차순 정렬
        CommentDto.CommentWithUser commentWithUser = new CommentDto.CommentWithUser();
        commentWithUser.setAccessUser(username);
        commentWithUser.setComment(commentRepository.findAllByPost(post, pageable).map(CommentDto::fromEntity));
        return commentWithUser;
    }

    public ResponseDto updateComment(UpdateCommentDto request, Long postId, Long commentId, String username) {
        User user = this.getUser(username);
        Post post = this.getPost(postId);
        Comment comment = this.getComment(commentId, post, user);

        if (request.contentIsNotModified(comment.getContent()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "댓글의 수정사항이 없습니다.");

        comment.updateComment(request);
        commentRepository.save(comment);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("댓글 수정이 완료되었습니다.");
        responseDto.setStatus(HttpStatus.OK);
        return responseDto;
    }

    public ResponseDto deleteComment(Long postId, Long commentId, String username) {
        User user = this.getUser(username);
        Post post = this.getPost(postId);
        Comment comment = getComment(commentId, post, user);
        commentRepository.delete(comment);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("댓글 삭제가 완료되었습니다.");
        responseDto.setStatus(HttpStatus.OK);
        return responseDto;
    }

    public User getUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자를 찾을 수 없습니다.");
        return optionalUser.get();
    }

    public Post getPost(Long postId, User user) {
        Optional<Post> optionalPost = postRepository.findByIdAndUser(postId, user);
        if (optionalPost.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found");
        return optionalPost.get();
    }

    public Post getPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found");
        return optionalPost.get();
    }

    public Comment getComment(Long commentId, Post post, User user) {
        Optional<Comment> optionalComment = commentRepository.findByIdAndPostAndUser(commentId, post, user);
        if (optionalComment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "comment not found");
        }
        return optionalComment.get();
    }
}
