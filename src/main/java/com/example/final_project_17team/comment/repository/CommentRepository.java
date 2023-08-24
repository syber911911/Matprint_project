package com.example.final_project_17team.comment.repository;

import com.example.final_project_17team.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository<Comment, Long> {
}
