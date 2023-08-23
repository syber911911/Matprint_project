package com.example.final_project_17team.service;

import com.example.final_project_17team.dto.RestaurantSearchDto;
import com.example.final_project_17team.exception.CustomException;
import com.example.final_project_17team.exception.ErrorCode;
import com.example.final_project_17team.repository.RestaurantRepository;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

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
}
