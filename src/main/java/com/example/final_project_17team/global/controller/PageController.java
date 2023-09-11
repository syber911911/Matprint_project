package com.example.final_project_17team.global.controller;

import jakarta.persistence.Entity;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/matprint")
public class PageController {

    @GetMapping("/named")
    public ModelAndView named() {
        return new ModelAndView("aaaa/named");
    }

    @GetMapping("/search")
    public ModelAndView search() {
        return new ModelAndView("aaaa/search");
    }

    @GetMapping("/mate")
    public ModelAndView mate() {
        return new ModelAndView("mate");
    }

    @GetMapping("/main")
    public ModelAndView main() {
        return new ModelAndView("main");
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping("/join")
    public ModelAndView join() {
        return new ModelAndView("join");
    }

    @GetMapping("/myPage")
    public ModelAndView myPage() {
        return new ModelAndView("myPage/myPage");
    }

    @GetMapping("/mate/create")
    public ModelAndView createdPost() {
        return new ModelAndView("post/create");
    }

    @GetMapping("/mate/{postId}")
    public ModelAndView postDetail(
            @PathVariable String postId
    ) {
        return new ModelAndView("post/detail");
    }

    @GetMapping("/mate/{postId}/comment")
    public ModelAndView comment(
            @PathVariable String postId
    ) {
        return new ModelAndView("post/detail");
    }
}