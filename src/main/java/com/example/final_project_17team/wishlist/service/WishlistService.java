package com.example.final_project_17team.wishlist.service;

import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.wishlist.dto.WishlistDto;
import com.example.final_project_17team.wishlist.entity.Wishlist;
import com.example.final_project_17team.wishlist.repository.WishlistRepository;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class WishlistService {
    private final RestaurantRepository restaurantRepository;
    private final WishlistRepository wishListRepository;
    private final UserRepository userRepository;

    // 상세 페이지 진입
    // 1. front : login 되지 않은 사용자
    //      1-1. 빈 별표를 보여줌
    //      1-2. wish 등록 요청 거부

    // 2. front : login 된 사용자
    //      1-1. wish 조회 요청을 보냄 (with access token)
    //      1-2. 조회 결과에 따라 별 ui 변화
    //      1-3. 사용자의 별 ui 클릭 시 기존에 등록된 식당이면 삭제, 아니라면 등록
    //      1-4. ui 변경

    // 음식점 상세 페이지에 진입한 사용자가 현재 식당을 wishlist 에
    // 등록했는지 판단
    public Boolean isMyWish(String username, Long restaurantId) {
        // 해당 사용자 조회
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자가 존재하지 않습니다.");
        User user = optionalUser.get();

        // 해당 식당 조회
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        if (optionalRestaurant.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 음식점이 존재하지 않습니다.");
        Restaurant restaurant = optionalRestaurant.get();

        // 해당 사용자 & 식당으로 등록된 위시리스트 조회
        Optional<Wishlist> optionalWishList = wishListRepository.findByRestaurantAndUser(restaurant, user);
        return optionalWishList.isPresent();
    }

    // 사용자의 wishlist 조회
    public List<WishlistDto> readMyWishlist(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자가 존재하지 않습니다.");
        User user = optionalUser.get();

        List<Wishlist> wishlists = wishListRepository.findAllByUser(user);
        if (wishlists.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사용자가 등록한 위시리스트가 존재하지 않습니다.");

        return WishlistDto.fromEntityList(wishlists);
    }

    // 위시리스트 등록
    public Boolean setMyWish(String username, Long restaurantId) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자가 존재하지 않습니다.");
        User user = optionalUser.get();

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        if (optionalRestaurant.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 음식점이 존재하지 않습니다.");
        Restaurant restaurant = optionalRestaurant.get();

        Optional<Wishlist> optionalMyRestaurant = wishListRepository.findByRestaurantIdAndUser(restaurantId, user); // 위시리스트에 있는지 확인

        if (optionalMyRestaurant.isEmpty()) {  // 만약 위시리스트에 유저와 맛집 id가 일치하는게 없다면 위시리스트에 없는 식당이므로 위시리스트에 넣어줌
            wishListRepository.save(
                    Wishlist.builder()
                            .user(user)
                            .restaurant(restaurant)
                            .build()
            );
            return true;
        } else {   // 이미 위시리스트에 있는데 버튼을 누른거라면 위시리스트에서 삭제시켜줌
            wishListRepository.delete(optionalMyRestaurant.get());
            return false;
        }
    }
}
