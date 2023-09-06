package com.example.final_project_17team.post.repository;


import com.example.final_project_17team.post.entity.Post;
import com.example.final_project_17team.user.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Optional<Post> findByIdAndUser(Long PostId, User user);
    List<Post> findByUser(User user);
    void deleteAllByUser(User user);
}
