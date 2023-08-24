package com.example.final_project_17team.comment.repository;

import com.example.final_project_17team.comment.entity.Comment;
import com.example.final_project_17team.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByRestaurantIdAndIdAndUser(Long restaurantId, Long commentId, User user);
}
