package com.example.final_project_17team.post.repository;

import com.example.final_project_17team.post.entity.Comment;
import com.example.final_project_17team.post.entity.Post;
import com.example.final_project_17team.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPost(Post post, Pageable pageable);
    Optional<Comment> findByIdAndPostAndUser(Long commentId, Post post, User user);
    void deleteAllByUser(User user);
}
