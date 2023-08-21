package com.example.final_project_17team.service;

import com.example.final_project_17team.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Slf4j
@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public String searchRestaurant(String target) throws IOException {
        String request = "http://map.naver.com/v5/api/search?caller=pcweb&query=" + target;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet getRequest = new HttpGet(request);

        getRequest.addHeader("User-Agent", "Mozila/5.0");
        getRequest.addHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(getRequest);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }
}
