package com.example.final_project_17team.post.service;

import com.example.final_project_17team.post.entity.Post;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecification {
    public static Specification<Post> containsTitle(String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%"+title+"%");
    }

    public static Specification<Post> containsContent(String content) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("content"), "%"+content+"%");
    }
    public static Specification<Post> equalGender(String gender) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("gender"), gender);
    }

    public static Specification<Post> inBoundAge(Integer age) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("user").get("age"), age, age + 9);
    }

    public static Specification<Post> equalUsername(String username) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("username"),  username);
    }

    public static Specification<Post> equalStatus(String status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }
}
