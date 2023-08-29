package com.example.final_project_17team.dataUpdate.service;

import com.example.final_project_17team.category.entity.Category;
import com.example.final_project_17team.category.repository.CategoryRepository;
import com.example.final_project_17team.dataUpdate.dto.PlaceDataDto;
import com.example.final_project_17team.restaurant.repository.RestaurantImageRepository;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.entity.RestaurantImage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoryUpdate {
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantImageRepository restaurantImageRepository;
    private WebDriver driver = null;
    private static final String playlistUrl = "https://youtube.com/playlist?list=PLuMuHAJh9g_Py_PSm8gmHdlcil6CQ9QCM";

    public CategoryUpdate(RestaurantRepository restaurantRepository, CategoryRepository categoryRepository, RestaurantImageRepository restaurantImageRepository) {
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
        this.restaurantImageRepository = restaurantImageRepository;
    }

    public void insertPlaceData() throws InterruptedException {
        List<List<String>> descriptionAndUrlList = this.getDescriptionAndUrl(this.process());

        for (List<String> descriptionAndUrl : descriptionAndUrlList) {
            PlaceDataDto placeDataDto = this.getPlaceData(descriptionAndUrl.get(0));
            Thread.sleep(3000);
            if (placeDataDto == null) {
                // 해당 가게의 검색 결과가 없음.
                log.info("{} : 검색 불가", descriptionAndUrl.get(0));
                continue;
            }
            Optional<Restaurant> optionalRestaurant = restaurantRepository.findByNameAndMapXAndMapY(placeDataDto.getName(), placeDataDto.getX(), placeDataDto.getY());
            if (optionalRestaurant.isEmpty()) {
                Restaurant restaurant = restaurantRepository.save(
                        Restaurant.builder()
                                .status("정상영업")
                                .name(placeDataDto.getName())
                                .tel(placeDataDto.getTel())
                                .openHours(placeDataDto.getOpenHours())
                                .closeHours(placeDataDto.getCloseHours())
                                .location(placeDataDto.getShortAddress().isEmpty() ? null : placeDataDto.getShortAddress().get(0))
                                .address(placeDataDto.getAddress())
                                .roadAddress(placeDataDto.getRoadAddress())
                                .mapX(placeDataDto.getX())
                                .mapY(placeDataDto.getY())
                                .menuInfo(placeDataDto.getMenuInfo())
                                .avgRatings(null)
                                .build()
                );
                log.info("{} restaurant 생성", placeDataDto.getName());

                List<Category> categoryList = new ArrayList<>();
                for (String category : placeDataDto.getCategory()) {
                    categoryList.add(
                            Category.builder()
                                    .name(category)
                                    .refUrl(null)
                                    .restaurant(restaurant)
                                    .build()
                    );
                    log.info("{} category 생성", category);
                }
                categoryList.add(
                        Category.builder()
                                .name("성시경 먹을텐데")
                                .refUrl(descriptionAndUrl.get(1))
                                .restaurant(restaurant)
                                .build()
                );
                log.info("{} category 생성", "성시경 먹을텐데");
                categoryRepository.saveAll(categoryList);

                List<RestaurantImage> restaurantImageList = new ArrayList<>();
                for (String restaurantImage : placeDataDto.getThumUrls()) {
                    restaurantImageList.add(
                            RestaurantImage.builder()
                                    .url(restaurantImage)
                                    .restaurant(restaurant)
                                    .build()
                    );
                }
                restaurantImageRepository.saveAll(restaurantImageList);
            }
        }
    }

    // 리팩토링 진행 후 맛집 찾기 기능에서도 활용 하도록 변경 필요
    public PlaceDataDto getPlaceData(String description) {
        log.info("search description : {}", description);
        String url = "https://map.naver.com/v5/api/search";
        WebClient webClient = WebClient.builder()
                .baseUrl("https://map.naver.com/v5/api")
                .defaultHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Whale/3.21.192.22 Safari/537.36")
                .build();
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("caller", "pcweb")
                        .queryParam("query", description)
                        .queryParam("type", "all")
                        .queryParam("page", "1")
                        .queryParam("displayCount", "1")
                        .queryParam("lang", "ko")
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .retry(3)
                .block();
        try {
            JsonNode originJson = new ObjectMapper().readTree(response);
            JsonNode place = originJson.get("result").get("place").get("list").get(0);

            PlaceDataDto placeDataDto = new ObjectMapper().treeToValue(place, PlaceDataDto.class);
            String businessHours = place.get("businessStatus").get("businessHours").textValue();
            String openHours = businessHours != null && !businessHours.isBlank() ? businessHours.split("~")[0].substring(8) : null;
            String closeHours = businessHours != null && !businessHours.isBlank() ? businessHours.split("~")[1].substring(8) : null;
            placeDataDto.setOpenHours(openHours);
            placeDataDto.setCloseHours(closeHours);
            return placeDataDto;
        } catch (Exception ex) {
            log.warn("Error Message : {} | {} : failed", ex.getMessage(), description);
        }
        return null;
    }

    public List<String> process() {
        System.setProperty("webdriver.chrome.driver", "/Users/hjun/Desktop/chromedriver-mac-arm64/chromedriver");
        List<String> urlList = new ArrayList<>();
        driver = new ChromeDriver();

        try {
            urlList = getVideoList();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        driver.close();
        driver.quit();
        return urlList;
    }

    private List<String> getVideoList() throws InterruptedException {
        List<String> urllist = new ArrayList<>();
        driver.get(playlistUrl);
        Thread.sleep(1000);

        List<WebElement> elements = driver.findElements(By.id("video-title"));
        for (WebElement element : elements) {
            urllist.add(element.getAttribute("href"));
        }
        return urllist;
    }

    public List<List<String>> getDescriptionAndUrl(List<String> urlList) {
        List<List<String>> result = new ArrayList<>();
        List<String> descriptionAndUrl;
        int count = 0;
        for (String url : urlList) {
            Connection connection = Jsoup.connect(url);
            try {
                Document document = connection.get();
                Element element = document.getElementById("watch7-content");
                assert element != null;
                Elements descriptionElement = element.getElementsByAttributeValue("itemprop", "description");
                String rowDescription = descriptionElement.attr("content");
                System.out.println(rowDescription);
                if (rowDescription.indexOf("#") != 0) {
                    String description = rowDescription.split("#")[0];
                    if (countChar(description, "\\[") >= 2) {
                        String[] splitDescription = description.split("\\)");
                        for (String rowItem : splitDescription) {
                            descriptionAndUrl = new ArrayList<>();
                            String item = rowItem.split("\\(")[0];
                            item = item.replaceAll("]", " ").split("\\[")[1];
                            item = item.split("\s[0-9]")[0];
                            descriptionAndUrl.add(item);
                            descriptionAndUrl.add(url);
                            result.add(descriptionAndUrl);
                        }
                    } else {
                        descriptionAndUrl = new ArrayList<>();
                        String item = description.split("\\(")[0];
                        if (countChar(description, "\\[") != 0) {
                            try {
                                item = item.replaceAll("]", " ").split("\\[")[1];
                            } catch (ArrayIndexOutOfBoundsException ex) {
                                item = item.replaceAll("]", " ").split("\\[")[0];
                            }
                            item = item.split("\s[0-9]")[0];
                        }
                        descriptionAndUrl.add(item);
                        descriptionAndUrl.add(url);
                        result.add(descriptionAndUrl);
                    }
                }
                log.info("{} 진행", (double)++count / urlList.size() * 100);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public int countChar(String rowString, String target) {
        return rowString.length() - rowString.replaceAll(target, "").length();
    }
}
