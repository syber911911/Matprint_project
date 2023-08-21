package com.example.final_project_17team.service;

import com.example.final_project_17team.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Slf4j
@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public String searchRestaurant(String target) throws IOException {
        String request = "https://map.naver.com/v5/api/search?caller=pcweb&query="+target;

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(request);

        getRequest.setHeader("Accept", "application/octet-stream,application/json");
        getRequest.setHeader("Connection", "keep-alive");
        getRequest.setHeader("Content-Type", "application/json");

        CloseableHttpResponse response = client.execute(getRequest);
        return response.toString();
    }
}
