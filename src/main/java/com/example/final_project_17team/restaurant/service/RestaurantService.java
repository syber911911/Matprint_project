package com.example.final_project_17team.restaurant.service;

import com.example.final_project_17team.dataUpdate.dto.PlaceDataDto;
import com.example.final_project_17team.dataUpdate.service.CategoryUpdate;
import com.example.final_project_17team.myrestaurant.entity.MyRestaurant;
import com.example.final_project_17team.myrestaurant.repository.MyRestaurantRepository;
import com.example.final_project_17team.restaurant.dto.RestaurantDto;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.review.repository.ReviewRepository;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;


@Slf4j
@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MyRestaurantRepository myRestaurantRepository;
    private final CategoryUpdate categoryUpdate;

    // 사용자의 검색어를 기준으로
    // naver maps api 호출 후
    // 결과를 page 로 반환
    public Page<PlaceDataDto> searchRestaurant(String target, Integer pageNumber, Integer pageSize) {
        List<PlaceDataDto> placeDataDtoList = categoryUpdate.getPlaceList(target + " " + "음식점", 100);
        if (placeDataDtoList == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 검색어의 결과가 없음");
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

    // 사용자가 음식점 리스트 중 하나를 선택
    // 선택된 음식점의 이름, x좌표, y좌표 기준으로
    // DB 조회
    // 조회된 결과가 없다면 해당 음식점의 name 과 address 로 api 호출 후 DB 저장
    // 조회된 결과가 있다면 update 일자를 확인하고 update 일자가 30일 이상이라면 api 호출 결과로 update 후 반환
    public RestaurantDto detailPage(String name, String address, BigDecimal mapX, BigDecimal mapY) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findByNameAndMapXAndMapY(name, mapX, mapY);

        if (optionalRestaurant.isEmpty()) {
            address = address.split("\s[0-9]")[0];
            List<PlaceDataDto> placeDataDtoList = categoryUpdate.getPlaceList(name + " " + address, 1);
            if (placeDataDtoList == null) {
                log.warn("api search query : {}", address + " " + name);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류");
            }
            PlaceDataDto placeDataDto = placeDataDtoList.get(0);
            categoryUpdate.saveRestaurant(placeDataDto);
            return RestaurantDto.builder()
                    .name(placeDataDto.getName())
                    .tel(placeDataDto.getTel())
                    .openHours(placeDataDto.getOpenHours())
                    .closeHours(placeDataDto.getCloseHours())
                    .address(placeDataDto.getAddress())
                    .roadAddress(placeDataDto.getRoadAddress())
                    .menuInfo(placeDataDto.getMenuInfo())
                    .mapX(placeDataDto.getX())
                    .mapY(placeDataDto.getY())
                    .avgRatings(0.0f)
                    .restaurantImageList(placeDataDto.getThumUrls())
                    .categoryList(placeDataDto.getCategory())
                    .build();
        } else {
            Restaurant restaurant = optionalRestaurant.get();
            // 조회된 결과가 있다면 update 일자를 확인하고 update 일자가 30일 이상이라면 api 호출 결과로 update 후 반환
            return RestaurantDto.fromEntity(restaurant);
        }
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
