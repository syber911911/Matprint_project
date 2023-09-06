package com.example.final_project_17team.global.service;

import com.example.final_project_17team.category.entity.Category;
import com.example.final_project_17team.category.repository.CategoryRepository;
import com.example.final_project_17team.global.dto.PlaceDataDto;
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

    // naver maps api 조회 결과를 DB 에 저장
    public Restaurant saveRestaurant(PlaceDataDto placeDataDto) {
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
        List<Category> categories = categoryRepository.saveAllAndFlush(categoryList);

        List<RestaurantImage> restaurantImageList = new ArrayList<>();
        for (String restaurantImage : placeDataDto.getThumUrls()) {
            restaurantImageList.add(
                    RestaurantImage.builder()
                            .url(restaurantImage)
                            .restaurant(restaurant)
                            .build()
            );
        }
        List<RestaurantImage> restaurantImages = restaurantImageRepository.saveAllAndFlush(restaurantImageList);
        restaurant.setCategoryAndImage(categories, restaurantImages);
        return restaurant;
    }

    // naver maps api 조회 결과와 검색어, 출처 url 을 함께 DB 에 저장
    public void saveRestaurant(PlaceDataDto placeDataDto, String searchTopic, String refUrl) {
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
        if (!searchTopic.isBlank()) {
            categoryList.add(
                    Category.builder()
                            .name(searchTopic)
                            .refUrl(refUrl)
                            .restaurant(restaurant)
                            .build()
            );
            log.info("{} category 생성", "성시경 먹을텐데");
        }
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

    // 크롤링한 맛집 데이터를 기반으로
    // naver maps api 호출 후
    // DB 저장
    public void searchAndSaveRestaurant() throws InterruptedException {
        List<List<String>> descriptionAndUrlList = this.getDescriptionAndUrl(this.process());

        for (List<String> descriptionAndUrl : descriptionAndUrlList) {
            List<PlaceDataDto> placeDataDtoList = this.getPlaceList(descriptionAndUrl.get(0) , 1);
            if (placeDataDtoList == null) {
                log.info("{} : 검색 불가", descriptionAndUrl.get(0));
                continue;
            }
            PlaceDataDto placeDataDto = placeDataDtoList.get(0);
            Thread.sleep(3000);

            Optional<Restaurant> optionalRestaurant = restaurantRepository.findByNameAndAddress(placeDataDto.getName(), placeDataDto.getAddress());
            if (optionalRestaurant.isEmpty()) {
                this.saveRestaurant(placeDataDto, "성시경 먹을텐데", descriptionAndUrl.get(1));
            }
        }
    }

    // target 을 query 로
    // naver maps api 호출
    // 결과를 리스트에 저장해 반환
    public List<PlaceDataDto> getPlaceList(String target, Integer displayCount) {
        List<PlaceDataDto> placeDataDtoList = new ArrayList<>();
        String url = "https://map.naver.com/v5/api";
        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Whale/3.21.192.22 Safari/537.36")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)
                )
                .build();
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("caller", "pcweb")
                        .queryParam("query", target)
                        .queryParam("type", "all")
                        .queryParam("page", 1)
                        .queryParam("displayCount", displayCount)
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
                if (placeDataDto == null)
                    continue;
                String businessHours = place.get("businessStatus").get("businessHours").textValue();
                String openHours = businessHours != null && !businessHours.isBlank() ? businessHours.split("~")[0].substring(8) : null;
                String closeHours = businessHours != null && !businessHours.isBlank() ? businessHours.split("~")[1].substring(8) : null;
                placeDataDto.setOpenHours(openHours);
                placeDataDto.setCloseHours(closeHours);
                placeDataDtoList.add(placeDataDto);
            }
            return placeDataDtoList;
        } catch (Exception ex) {
            log.warn("Error message : {} | {} : failed", ex.getMessage(), target);
        }
        return null;
    }

    // chrome webDriver 실행
    // 영상 url 크롤링 후 webDriver 종료
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

    // chrome webDriver 를 통해
    // 유튜브 재생목록의 모든 영상 url 을 크롤링
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

    // 유튜브 재생목록의 각 영상의 url 을 통해
    // 크롤링 진행
    // 현재는 성시경의 먹을텐데 재생목록에 맞춰져서 진행되었음
    // [description(가게이름 + 주소), url(영상 url)]
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

    // rowString 에 target 문자열이 얼마나 포함되어 있는지 반환
    public int countChar(String rowString, String target) {
        return rowString.length() - rowString.replaceAll(target, "").length();
    }
}
