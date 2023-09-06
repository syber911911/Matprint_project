package com.example.final_project_17team.review.service;

import com.example.final_project_17team.global.dto.ResponseDto;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.review.dto.ReadReviewDto;
import com.example.final_project_17team.review.dto.CreateReviewDto;
import com.example.final_project_17team.review.dto.UpdateReviewDto;
import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.review.repository.ReviewRepository;
import com.example.final_project_17team.reviewImages.entity.ReviewImages;
import com.example.final_project_17team.reviewImages.repository.ReviewImagesRepository;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImagesRepository reviewImagesRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public ResponseDto createReview(String username, Long restaurantId, CreateReviewDto request) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자입니다.");
        User user = optionalUser.get();

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
        if (optionalRestaurant.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 음식점을 찾을 수 없습니다.");
        Restaurant restaurant = optionalRestaurant.get();

        // 첨부한 이미지가 존재하지 않는 경우
        if (CollectionUtils.isEmpty(request.getImageList()) || request.getImageList().get(0).isEmpty()) {
            reviewRepository.save(
                    Review.builder()
                            .content(request.getContent())
                            .ratings(request.getRatings())
                            .user(user)
                            .restaurant(restaurant)
                            .build()
            );
            ResponseDto responseDto = new ResponseDto();
            responseDto.setStatus(HttpStatus.OK);
            responseDto.setMessage("리뷰 등록이 완료되었습니다.");
            return responseDto;
        }

        // 첨부한 이미지가 존재하는 경우
        Review review = reviewRepository.save(
                Review.builder()
                        .content(request.getContent())
                        .ratings(request.getRatings())
                        .user(user)
                        .restaurant(restaurant)
                        .build()
        );
        this.saveImage(request.getImageList(), user.getId(), review);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatus(HttpStatus.OK);
        responseDto.setMessage("리뷰 등록이 완료되었습니다.");
        return responseDto;
    }

    public ReadReviewDto.ReadReviewWithUser readReviewPage(String username, Long restaurantId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending()); // 댓글은 최신 순으로 나오게?
        Page<Review> reviewPage = reviewRepository.findAllByRestaurantId(restaurantId, pageable);

        ReadReviewDto.ReadReviewWithUser readReviewWithUser = new ReadReviewDto.ReadReviewWithUser();
        // 현재 해당 요청을 보낸 사용자의 정보를 포함해 함께 반환
        // 사용자에 따라 ui 변화를 주기 위해
        readReviewWithUser.setAccessUsername(username);
        readReviewWithUser.setReviewPage(reviewPage.map(ReadReviewDto::fromEntity));
        return readReviewWithUser;
    }

    // 리뷰 삭제
    public ResponseDto deleteReview(String username, Long restaurantId, Long reviewId) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다.");
        User user = optionalUser.get();

        Optional<Review> optionalReview = reviewRepository.findByRestaurantIdAndIdAndUser(restaurantId, reviewId, user);
        if (optionalReview.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 항목을 찾을 수 없습니다.");
        Review review = optionalReview.get();
        this.deleteDir(user.getId(), review);
        reviewRepository.delete(review);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatus(HttpStatus.OK);
        responseDto.setMessage("리뷰 삭제가 완료되었습니다.");
        return responseDto;
    }

    public ResponseDto updateReview(String username, Long restaurantId, Long reviewId, UpdateReviewDto request) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자가 존재하지 않습니다.");
        User user = optionalUser.get();

        Optional<Review> optionalReview = reviewRepository.findByRestaurantIdAndIdAndUser(restaurantId, reviewId, user);
        if (optionalReview.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 항목을 찾을 수 없습니다.");
        Review review = optionalReview.get();

        if (request.isNotModified(review))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "리뷰의 수정사항이 없습니다.");

        if (!request.contentIsNotModified(review.getContent()) || !request.ratingsIsNotModified(review.getRatings())) {
            review.updateContentAndRatings(request.getContent(), request.getRatings());
            reviewRepository.save(review);
        }

        if (!request.addImageIsNull())
            this.saveImage(request.getAddImageList(), user.getId(), review);

        if (!request.deleteImageIsNull()) {
            this.deleteImage(request.getDeleteImageList(), user.getId(), review);
        }
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("리뷰 수정이 완료되었습니다.");
        responseDto.setStatus(HttpStatus.OK);
        return responseDto;
    }


    public void saveImage(List<MultipartFile> imageList, Long userId, Review review) {
        // 이미지 저장 디렉토리 생성
        String imageDir = String.format("media/%d/review/%d/", userId, review.getId());
        try {
            Files.createDirectories(Path.of(imageDir));
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "디렉토리 생성에 실패");
        }
        // 이미지 저장
        List<ReviewImages> reviewImagesList = new ArrayList<>();

        for (MultipartFile image : imageList) {
            LocalDateTime createTime = LocalDateTime.now();
            String originalFileName = image.getOriginalFilename();
            String[] fileNameSplit = originalFileName.split("\\.");
            String extension = fileNameSplit[fileNameSplit.length - 1];
            String reviewImageFileName = String.format("%s_%s.%s", createTime.toString(), review.getId(), extension);
            String reviewImagePath = imageDir + reviewImageFileName;

            try {
                image.transferTo(Path.of(reviewImagePath));
            } catch (IOException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 실패");
            }

            ReviewImages reviewImages = ReviewImages.builder()
                    .imageUrl(String.format("/static/%d/review/%d/%s", userId, review.getId(), reviewImageFileName))
                    .review(review)
                    .build();
            reviewImagesList.add(reviewImages);
        }
        reviewImagesRepository.saveAll(reviewImagesList);
    }

    public void deleteImage(List<String> imageList, Long userId, Review review) {
        List<ReviewImages> deleteReviewImagesList = new ArrayList<>();
        for (String image : imageList) {
            Optional<ReviewImages> optionalReviewImages = reviewImagesRepository.findByReviewAndImageUrl(review, image);
            if (optionalReviewImages.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제 항목이 옳바르지 않습니다.");
            ReviewImages reviewImages = optionalReviewImages.get();

            String[] splitFilePath = image.split("/");
            String filename = splitFilePath[splitFilePath.length - 1];
            String targetPath = String.format("media/%d/review/%d/%s", userId, review.getId(), filename);
            File file = new File(targetPath);
            if (!file.delete()) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다");
            deleteReviewImagesList.add(reviewImages);
        }
        reviewImagesRepository.deleteAll(deleteReviewImagesList);
    }

    public void deleteDir(Long userId, Review review) {
        String imageDir = String.format("media/%d/review/%d", userId, review.getId());
        File targetDir = new File(imageDir);
        if (targetDir.exists()) { // 해당 경로에 파일 or 디렉토리가 존재하는지
            File[] images = targetDir.listFiles();
            assert images != null; // 디렉토리는 존재하지만 파일은 없는 경우도 존재 가능
            for (File image : images) {
                if (!image.delete()) log.warn("{} 파일의 삭제가 정상적으로 처리되지 않았습니다.", image.getPath());
            }
        } else log.info("{} 해당 경로에 디렉토리 혹은 파일이 존재하지 않습니다.", targetDir.getPath());
        if (!targetDir.delete()) log.warn("{} 디렉토리의 삭제가 정상적으로 처리되지 않았습니다.", targetDir.getPath());
    }
}
