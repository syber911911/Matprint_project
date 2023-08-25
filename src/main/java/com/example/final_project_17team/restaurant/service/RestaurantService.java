package com.example.final_project_17team.restaurant.service;

import com.example.final_project_17team.global.exception.CustomException;
import com.example.final_project_17team.global.exception.ErrorCode;
import com.example.final_project_17team.restaurant.dto.RestaurantSearchDto;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
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
import org.openqa.selenium.json.Json;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class RestaurantService {

    public Page<RestaurantSearchDto> searchRestaurant(String target, int pageNum) throws IOException {
        String request = "https://map.naver.com/v5/api/search?caller=pcweb&query=" + target + "음식점&displayCount=100";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet getRequest = new HttpGet(request);

        getRequest.addHeader("User-Agent", "Mozila/5.0");
        getRequest.addHeader("Content-type", "application/json");
        CloseableHttpResponse response = client.execute(getRequest);
        return parseAndPaged(EntityUtils.toString(response.getEntity(), "UTF-8"), pageNum);
    }

    public Page<RestaurantSearchDto> parseAndPaged(String response, int pageNum) throws JsonProcessingException {
        JsonNode listNode = new ObjectMapper().readTree(response).get("result").get("place").get("list");
        List<RestaurantSearchDto> restaurants = new ArrayList<>();

        if (listNode.size() <= pageNum * 10)
            throw new CustomException(ErrorCode.PAGE_NUMBER_OUT_OF_BOUNDS, "The response only got " + listNode.size() + " places");

        for(JsonNode node: listNode) {
            restaurants.add(new ObjectMapper().treeToValue(node, RestaurantSearchDto.class));
            restaurants.get(restaurants.size()-1).setBusinessHours(
                    node.get("businessStatus").get("businessHours").asText()
            );
        }
        Pageable pageResponse = PageRequest.of(pageNum, 10);
        int endIdx = Math.min(pageNum * 10 + 10, restaurants.size());
        return new PageImpl<>(restaurants.subList(pageNum * 10, endIdx), pageResponse, restaurants.size());
    }
}
