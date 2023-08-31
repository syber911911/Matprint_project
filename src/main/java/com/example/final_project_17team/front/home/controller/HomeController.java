package com.example.final_project_17team.front.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String mainPage(Model model) {
        model.addAttribute("currentDateTime", java.time.LocalDateTime.now());
        return "home";
    }
}
