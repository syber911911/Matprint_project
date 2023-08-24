package com.example.final_project_17team.restaurant.service;

import com.example.final_project_17team.comment.entity.Comment;
import com.example.final_project_17team.comment.repository.CommentRepository;
import com.example.final_project_17team.restaurant.dto.RestaurantSearchDto;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public List<RestaurantSearchDto> searchRestaurant(String target, int pageNum) throws IOException {
        String request = "https://map.naver.com/v5/api/search?caller=pcweb&query=" + target + "음식점";

        // HttpClient 이용 외부 api 호출
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet getRequest = new HttpGet(request);

        getRequest.addHeader("User-Agent", "Mozila/5.0");
        getRequest.addHeader("Content-type", "application/json");
        CloseableHttpResponse response = client.execute(getRequest);

        return parseAndListed(EntityUtils.toString(response.getEntity(), "UTF-8"), pageNum);
    }

    public List<RestaurantSearchDto> parseAndListed(String response, int pageNum) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(response).get("result").get("place").get("list");
        List<RestaurantSearchDto> restaurants = new ArrayList<>();

        for(JsonNode node: jsonNode) {
            restaurants.add(new ObjectMapper().treeToValue(node, RestaurantSearchDto.class));
            restaurants.get(restaurants.size()-1).setBusinessHours(
                    node.get("businessStatus").get("businessHours").asText()
            );
        }
        return restaurants;
    }




    // 리뷰 삭제 soft delete로 구현
    public boolean deleteReview(Long restaurantId, Long commentId){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        User user = optionalUser.get();

        Optional<Comment> optionalComment = commentRepository.findByRestaurantIdAndIdAndUser(restaurantId, commentId, user);

        if (optionalComment.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Comment comment = optionalComment.get();

        comment.setDeleted(true);
        commentRepository.save(comment);

        return true;
    }
}
