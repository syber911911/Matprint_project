package com.example.final_project_17team.restaurant.service;

import com.example.final_project_17team.comment.entity.Comment;
import com.example.final_project_17team.comment.repository.CommentRepository;
import com.example.final_project_17team.myrestaurant.entity.MyRestaurant;
import com.example.final_project_17team.myrestaurant.repository.MyRestaurantRepository;
import com.example.final_project_17team.restaurant.dto.RestaurantDto;
import com.example.final_project_17team.restaurant.dto.RestaurantSearchDto;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.review.repository.ReviewRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MyRestaurantRepository myRestaurantRepository;

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

    public RestaurantDto detailPage(Long id) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(id);

        if (optionalRestaurant.isPresent()) {
            return RestaurantDto.fromEntity(optionalRestaurant.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }


    // 리뷰 삭제 soft delete로 구현
    public boolean deleteReview(Long restaurantId, Long reviewId){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        User user = optionalUser.get();

        Optional<Review> optionalReview = reviewRepository.findByRestaurantIdAndIdAndUser(restaurantId, reviewId, user);

        if (optionalReview.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Review review = optionalReview.get();

        review.setDeleted(true);
        reviewRepository.save(review);

        return true;
    }

    public int wishlistButton(Long restaurantId){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        User user = optionalUser.get();

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId); // restaurant id 에 해당하는 음식점 찾기
        if(optionalRestaurant.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Restaurant restaurant = optionalRestaurant.get();

        Optional<MyRestaurant> optionalMyRestaurant = myRestaurantRepository.findByRestaurantIdAndUser(restaurantId, user); // 위시리스트에 있는지 확인

        if(optionalMyRestaurant.isEmpty()){  // 만약 위시리스트에 유저와 맛집 id가 일치하는게 없다면 위시리스트에 없는 식당이므로 위시리스트에 넣어줌
            MyRestaurant myRestaurant = new MyRestaurant();
            myRestaurant.setUser(user);
            myRestaurant.setRestaurant(restaurant);
            myRestaurantRepository.save(myRestaurant);
            return 1;
        }
        else{   // 이미 위시리스트에 있는데 버튼을 누른거라면 위시리스트에서 삭제시켜줌
            MyRestaurant myRestaurant = optionalMyRestaurant.get();
            myRestaurantRepository.delete(myRestaurant);
            myRestaurantRepository.save(myRestaurant);
            return 2;
        }

    }
}
