package com.example.final_project_17team.restaurant.service;

import com.example.final_project_17team.dataUpdate.dto.PlaceDataDto;
import com.example.final_project_17team.myrestaurant.entity.MyRestaurant;
import com.example.final_project_17team.myrestaurant.repository.MyRestaurantRepository;
import com.example.final_project_17team.restaurant.dto.RestaurantDto;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.review.dto.ReviewPageDto;
import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.review.repository.ReviewRepository;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;


@Slf4j
@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MyRestaurantRepository myRestaurantRepository;

    public List<PlaceDataDto> getPlace(String target) {
        List<PlaceDataDto> placeDataDtoList = new ArrayList<>();
        String url = "https://map.naver.com/v5/api";
        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Whale/3.21.192.22 Safari/537.36")
                .build();
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("caller", "pcweb")
                        .queryParam("query", target + " " + "음식점")
                        .queryParam("type", "all")
                        .queryParam("page", "1")
                        .queryParam("displayCount", "100")
                        .queryParam("lang", "ko")
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .retry(3)
                .block();
        try {
            JsonNode originJson = new ObjectMapper().readTree(response);
            JsonNode placeList = originJson.get("result").get("place").get("list");

            for (JsonNode place : placeList) {
                PlaceDataDto placeDataDto = new ObjectMapper().treeToValue(place, PlaceDataDto.class);
                String businessHours = place.get("businessStatus").get("businessHours").textValue();
                String openHours = businessHours != null && !businessHours.isBlank() ? businessHours.split("~")[0].substring(8) : null;
                String closeHours = businessHours != null && !businessHours.isBlank() ? businessHours.split("~")[1].substring(8) : null;
                placeDataDto.setOpenHours(openHours);
                placeDataDto.setCloseHours(closeHours);
                placeDataDtoList.add(placeDataDto);
                log.info(placeDataDto.getName());
            }
            return placeDataDtoList;
        } catch (Exception ex) {
            log.warn("Error message : {} | {} : failed", ex.getMessage(), target);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Page<PlaceDataDto> searchRestaurant(String target, Integer pageNumber, Integer pageSize) {
        List<PlaceDataDto> placeDataDtoList = this.getPlace(target);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), placeDataDtoList.size());
        return new PageImpl<>(placeDataDtoList.subList(start, end), pageable, placeDataDtoList.size());
    }

//    public List<RestaurantSearchDto> searchRestaurant(String target, int pageNum) throws IOException {
//        String request = "https://map.naver.com/v5/api/search?caller=pcweb&query=" + target + "음식점";
//
//        // HttpClient 이용 외부 api 호출
//        CloseableHttpClient client = HttpClients.createDefault();
//        HttpGet getRequest = new HttpGet(request);
//
//        getRequest.addHeader("User-Agent", "Mozila/5.0");
//        getRequest.addHeader("Content-type", "application/json");
//        CloseableHttpResponse response = client.execute(getRequest);
//
//        return parseAndListed(EntityUtils.toString(response.getEntity(), "UTF-8"), pageNum);
//    }
//
//    public List<RestaurantSearchDto> parseAndListed(String response, int pageNum) throws JsonProcessingException {
//        JsonNode jsonNode = new ObjectMapper().readTree(response).get("result").get("place").get("list");
//        List<RestaurantSearchDto> restaurants = new ArrayList<>();
//
//        for(JsonNode node: jsonNode) {
//            restaurants.add(new ObjectMapper().treeToValue(node, RestaurantSearchDto.class));
//            restaurants.get(restaurants.size()-1).setBusinessHours(
//                    node.get("businessStatus").get("businessHours").asText()
//            );
//        }
//        return restaurants;
//    }

    public RestaurantDto detailPage(Long id) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(id);

        if (optionalRestaurant.isPresent()) {
            return RestaurantDto.fromEntity(optionalRestaurant.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // 모든 사람이 볼 수 있음
    public Page<ReviewPageDto> readReviewPage(Long restaurantId, Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(
                pageNumber, pageSize, Sort.by("createdAt").descending()); // 댓글은 최신 순으로 나오게?

        Page<Review> reviewPage
                = reviewRepository.findAllByRestaurantId(restaurantId, pageable);

        Page<ReviewPageDto> reviewPageDto
                = reviewPage.map(ReviewPageDto::fromEntity);

        return reviewPageDto;
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
