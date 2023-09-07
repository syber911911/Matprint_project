package com.example.final_project_17team.reviewImages.repository;

import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.reviewImages.entity.ReviewImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewImagesRepository extends JpaRepository<ReviewImages, Long> {
    Optional<ReviewImages> findByReviewAndImageUrl(Review review, String imageUrl);
}
