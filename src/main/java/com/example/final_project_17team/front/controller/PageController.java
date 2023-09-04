package com.example.final_project_17team.front.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    @GetMapping("/main")
    public String mainPage() {
        return "main";
    }

    @GetMapping("/users/login")
    public String loginPage() {
        return "login";
    }


}
