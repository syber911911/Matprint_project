package com.example.final_project_17team.post.service;

import com.example.final_project_17team.comment.dto.CommentDto;
import com.example.final_project_17team.comment.entity.Comment;
import com.example.final_project_17team.comment.repository.CommentRepository;
import com.example.final_project_17team.post.dto.PostDto;
import com.example.final_project_17team.post.entity.Post;
import com.example.final_project_17team.post.repository.PostRepository;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.review.dto.ReviewPageDto;
import com.example.final_project_17team.review.dto.ReviewRequestDto;
import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostDto createPost(PostDto dto, Long restaurantId){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        User user = optionalUser.get();

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        if(optionalRestaurant.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Restaurant restaurant = optionalRestaurant.get();

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setUserName(username);
        post.setContent(dto.getContent());
        post.setStatus("모집 중");
        post.setVisitDate(post.getVisitDate());
        post.setPrefer(post.getPrefer());
        post.setUserId(user.getId());
        post.setRestaurantId(restaurantId);

        postRepository.save(post);

        return PostDto.fromEntity(post);
    }

    // 수정은 제목, 내용, 방문날짜, 선호에 한해서만 됨
    public PostDto updatePost(PostDto dto, Long postId){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        User user = optionalUser.get();

        Optional<Post> optionalPost = postRepository.findByIdAndUserId(postId, user.getId());
        if(optionalPost.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Post post = optionalPost.get();

        if (!Objects.equals(dto.getTitle(), post.getTitle())) {
            post.setTitle(dto.getTitle());
        }

        if (!Objects.equals(dto.getContent(), post.getContent())) {
            post.setContent(dto.getContent());
        }

        if (!Objects.equals(dto.getVisitDate(), post.getVisitDate())) {
            post.setVisitDate(dto.getVisitDate());
        }

        if (!Objects.equals(dto.getPrefer(), post.getPrefer())) {
            post.setPrefer(dto.getPrefer());
        }

        post.setStatus("모집 중");
        postRepository.save(post);

        return PostDto.fromEntity(post);
    }

    public boolean deletePost(Long postId){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        User user = optionalUser.get();

        Optional<Post> optionalPost = postRepository.findByIdAndUserId(postId, user.getId());
        if(optionalPost.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Post post = optionalPost.get();

        post.setDeleted(true);
        postRepository.save(post);

        return true;
    }

    public Page<PostDto> readAllPostPage(Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(
                pageNumber, pageSize, Sort.by("createdAt").descending()); // 댓글은 최신 순으로 나오게?

        Page<Post> postPage
                = postRepository.findAll(pageable);

        Page<PostDto> postDto
                = postPage.map(PostDto::fromEntity);

        return postDto;
    }

    public List<PostDto> searchPost(String targets) {

        List<String> targetList = Arrays.asList(targets.split(" ")); // 사용자가 검색한 것을 공백을 기준으로 나눔

        List<Post> posts = postRepository.findAll();

        List<PostDto> postDtoList = new ArrayList<>();
        Post targetPost = new Post();

        for (Post post : posts){
            for (String target : targetList) {
                if (post.getTitle().contains(target) || post.getContent().contains(target)) { // 제목이나 내용에 키워드가 포함되어 있는 글만 가져오기
                    targetPost = post;
                    PostDto postDto = PostDto.fromEntity(targetPost);
                    postDtoList.add(postDto);
                }
                else throw new ResponseStatusException(HttpStatus.NOT_FOUND); // targets에 해당하는 글 없음
            }
        }

        return postDtoList;
    }

    public CommentDto crateComment(CommentDto dto, Long postId) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        User user = optionalUser.get();

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setUserId(user.getId());
        comment.setUserName(username);
        comment.setPostId(postId);
        commentRepository.save(comment);

        return CommentDto.fromEntity(comment);

    }

    //TODO 동행 조회(전체 조회와 제목, 내용에 키워드 포함 조회는 구현 했는데 gender, age, status 등으로 조회하는건 아직 구현 안함)
    //TODO 댓글 조회(동행 글 작성자와, 댓글 작성자 들만 볼 수 있게), 수정, 삭제
    //TODO stauts 변경(모집중 -> 모집완료)

}
