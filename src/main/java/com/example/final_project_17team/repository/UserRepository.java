package com.example.final_project_17team.repository;

import com.example.final_project_17team.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
