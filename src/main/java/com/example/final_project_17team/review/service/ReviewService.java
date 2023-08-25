package com.example.final_project_17team.review.service;

import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.review.dto.ReviewRequestDto;
import com.example.final_project_17team.review.dto.ReviewUpdateDto;
import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.review.repository.ReviewRepository;
import com.example.final_project_17team.reviewImages.entity.ReviewImages;
import com.example.final_project_17team.reviewImages.repository.ReviewImagesRepository;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImagesRepository reviewImagesRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public ReviewRequestDto createReview(Long restaurantId, ReviewRequestDto dto){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        User user = optionalUser.get();

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        if(optionalRestaurant.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Restaurant restaurant = optionalRestaurant.get();

        List<ReviewImages> imagesList = new ArrayList<>();

        Review review = new Review();
        review.setRestaurant(restaurant);
        review.setUser(user);
        review.setRatings(dto.getRatings());
        review.setTitle(dto.getTitle());
        review.setContent(dto.getContent());
        review.setReviewImages(imagesList);

        // 실제 저장되는 디렉토리 경로 : media/
        // 웹브라우저에서 불러올때 경로 : static/
        if(!(dto.getImageList() == null)){

            // media/restaurantId/1/review/1  식당 별 리뷰
            String reviewImageDir = String.format("media/restaurantId/%d/review/%d/",review.getRestaurant().getId(), review.getId());
            try {
                Files.createDirectories(Path.of(reviewImageDir));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            int i = 1;

            for(MultipartFile img : dto.getImageList()) {

                ReviewImages reviewImages = new ReviewImages();

                // 확장자를 포함한 이미지 이름 만들기
                String originalFilename = img.getOriginalFilename();   // 원래 이미지 이름
                String[] fileNameSplit = originalFilename.split("\\.");
                String extension = fileNameSplit[fileNameSplit.length - 1]; // 원래 이미지 확장자 .jpg
                String reviewImageFilename = username + "_" + i + "." + extension;  // 새로 만들어준 파일 이름
                i ++; // i를 증가시켜 다음 이미지에 대한 파일 이름 생성

                // 폴더와 파일 경로를 포함한 이름 만들기
                // media/restaurantId/1/review/1/user_1
                String reviewImagePath = reviewImageDir + reviewImageFilename;

                // MultipartFile 을 저장하기
                try {
                    img.transferTo(Path.of(reviewImagePath));
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }

                // /static/restaurant/1/review/user_1.jpg
                reviewImages.setImage_url(String.format("/static/restaurantId/%d/review/%s", review.getRestaurant().getId(), reviewImageFilename));
                reviewImages.setReview(review);
                reviewImagesRepository.save(reviewImages);

            }
        }

        reviewRepository.save(review);

        return ReviewRequestDto.fromEntity(review);
    }

    // 요청 받은 dto 내용으로 title, content 수정, 이미지는 추가로 들어감
    public void updateArticle(Long restaurantId, Long reviewId, ReviewUpdateDto dto){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        User user = optionalUser.get();

        Optional<Review> optionalReview = reviewRepository.findByRestaurantIdAndIdAndUser(restaurantId, reviewId, user);
        if(optionalReview.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Review review = optionalReview.get();

        review.setTitle(dto.getTitle());   // title. content, rating 수정, 이전과 내용이 같다면 안바꿔도 되지만 그냥 set 하였음
        review.setContent(dto.getContent());
        review.setRatings(dto.getRatings());

        int lastImageIndex = review.getReviewImages().size();

        if(!(dto.getUpdateImageList() == null))
            for(MultipartFile img : dto.getUpdateImageList()){
                String postImageDir = String.format("media/restaurantId/%d/review/%d/",review.getRestaurant().getId(), review.getId());

                try {
                    Files.createDirectories(Path.of(postImageDir));
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }

                // 확장자를 포함한 이미지 이름 만들기
                String originalFilename = img.getOriginalFilename();
                String[] fileNameSplit = originalFilename.split("\\.");
                String extension = fileNameSplit[fileNameSplit.length - 1];
                String reviewImageFilename = username + "_" + lastImageIndex + "." + extension;
                lastImageIndex ++; // lastImageIndex를 증가시켜 다음 이미지에 대한 파일 이름 생성


                String postImagePath = postImageDir + reviewImageFilename;
                try {
                    img.transferTo(Path.of(postImagePath));
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }

                ReviewImages images = new ReviewImages();
                images.setImage_url(String.format("/static/restaurantId/%d/review/%s", review.getRestaurant().getId(), reviewImageFilename));
                images.setReview(review);
                reviewImagesRepository.save(images);
                review.getReviewImages().add(images);
            }

        reviewRepository.save(review);


    }

    // 리뷰의 이미지 삭제하는 메소드
    public void deleteImage(Long restaurantId, Long reviewId, Long imageId) {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        User user = optionalUser.get();

        Optional<Review> optionalReview = reviewRepository.findByRestaurantIdAndIdAndUser(restaurantId, reviewId, user);
        if(optionalReview.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Review review = optionalReview.get();

        Optional<ReviewImages> optionalReviewImages = reviewImagesRepository.findById(imageId);
        if (optionalReviewImages.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        ReviewImages reviewImage = optionalReviewImages.get();

        if (!Objects.equals(reviewId, reviewImage.getReview().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String[] split = reviewImage.getImage_url().split("/");  // /static/restaurant/1/review/user_1.jpg
        String name = split[split.length - 1];   //user_1.jpg
        String imagePath = "media/restaurantId" + restaurantId + "review/" + reviewId + "/" + name;  //"media/restaurantId/1/review/1/user_1.jpg"

        // 실제 서버에서 이미지 삭제
        try {
            Files.delete(Path.of(imagePath));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

       reviewImagesRepository.deleteById(imageId); // 이미지는 서버에서도 삭제하므로 soft delete 안함

    }

}
