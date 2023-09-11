package com.example.final_project_17team.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/matprint")
public class PageController {

    @GetMapping("/named")
    public ModelAndView named() {
        return new ModelAndView("html/named");
    }

    @GetMapping("/search")
    public ModelAndView search() {
        return new ModelAndView("html/search");
    }

    @GetMapping("/myPage")
    public ModelAndView myPage() {
        return new ModelAndView("html/myPage");
    }

}