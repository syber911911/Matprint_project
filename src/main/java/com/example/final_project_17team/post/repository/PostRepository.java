package com.example.final_project_17team.post.repository;


import com.example.final_project_17team.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndUserId(Long PostId, Long userId);

    List<Post> findAllByUserId(Long userId);

    List<Post> findAllByDeletedAtIsNull();

}
