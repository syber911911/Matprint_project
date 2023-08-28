package com.example.final_project_17team.myrestaurant.service;

import com.example.final_project_17team.myrestaurant.dto.MyRestaurantDto;
import com.example.final_project_17team.myrestaurant.entity.MyRestaurant;
import com.example.final_project_17team.myrestaurant.repository.MyRestaurantRepository;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class MyRestaurantService {
    private final MyRestaurantRepository myRestaurantRepository;
    private final UserRepository userRepository;

    public List<MyRestaurantDto> myRestaurantView(Long myRestaurantId) {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        User user = optionalUser.get();
        Optional<MyRestaurant> optionalMyRestaurant = myRestaurantRepository.findByRestaurantIdAndUser(myRestaurantId, user);

        if (optionalMyRestaurant.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        MyRestaurant myRestaurant = optionalMyRestaurant.get();
        MyRestaurantDto restaurantDto = MyRestaurantDto.fromEntity(myRestaurant);

        List<MyRestaurantDto> restaurantDtoList = new ArrayList<>();
        restaurantDtoList.add(restaurantDto);

        return restaurantDtoList;
    }

    public void setVisited(Long myRestaurantId) {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        User user = optionalUser.get();
        Optional<MyRestaurant> optionalMyRestaurant = myRestaurantRepository.findByRestaurantIdAndUser(myRestaurantId, user);
        MyRestaurant myRestaurant = optionalMyRestaurant.get();

        myRestaurant.setVisited(true);
        myRestaurantRepository.save(myRestaurant);
    }

    public void deleteMyRestaurant(Long myRestaurantId) {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        User user = optionalUser.get();
        Optional<MyRestaurant> optionalMyRestaurant = myRestaurantRepository.findByRestaurantIdAndUser(myRestaurantId, user);

        if (optionalMyRestaurant.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        myRestaurantRepository.deleteById(myRestaurantId);
    }
}
