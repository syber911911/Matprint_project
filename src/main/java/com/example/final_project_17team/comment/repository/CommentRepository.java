package com.example.final_project_17team.comment.repository;

import com.example.final_project_17team.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPostId(Long postId, Pageable pageable);
    Optional<Comment> findByIdAndUserId(Long PostId, Long userId);
}
