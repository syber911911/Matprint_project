package com.example.final_project_17team.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {

    @GetMapping("/users/profiles")
    public String MyPage() {
        return "html/myPage";
    }
}

