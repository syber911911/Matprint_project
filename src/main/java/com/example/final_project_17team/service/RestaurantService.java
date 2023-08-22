package com.example.final_project_17team.service;

import com.example.final_project_17team.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Slf4j
@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public JSONArray searchRestaurant(String target) throws IOException, ParseException {
        String request = "https://map.naver.com/v5/api/search?caller=pcweb&query=" + target;

        // HttpClient 이용 외부 api 호출
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet getRequest = new HttpGet(request);

        getRequest.addHeader("User-Agent", "Mozila/5.0");
        getRequest.addHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(getRequest);

        // json string -> 레스토랑 리스트 추출
        String str = EntityUtils.toString(response.getEntity(), "UTF-8");
        JSONObject result = (JSONObject) ((JSONObject) new JSONParser().parse(str)).get("result");
        JSONObject place = (JSONObject) result.get("place");
        JSONArray lists = (JSONArray) place.get("list");

        return lists;
    }
}
