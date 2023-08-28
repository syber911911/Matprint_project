package com.example.final_project_17team.post.service;

import com.example.final_project_17team.post.dto.PostDto;
import com.example.final_project_17team.post.entity.Post;
import com.example.final_project_17team.post.repository.PostRepository;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.review.dto.ReviewRequestDto;
import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

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

}
