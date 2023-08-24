package com.example.final_project_17team.category.repository;

import com.example.final_project_17team.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
