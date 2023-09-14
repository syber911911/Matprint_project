package com.example.final_project_17team.global.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class PageController {
    @GetMapping("/check")
    public void check() {
        throw new ResponseStatusException(HttpStatus.OK);
    }

    @GetMapping("/named")
    public ModelAndView named() {
        return new ModelAndView("html/named");
    }

    @GetMapping("/search")
    public ModelAndView search() {
        return new ModelAndView("html/search");
    }

    @GetMapping("/mate")
    public ModelAndView mate() {
        return new ModelAndView("mate");
    }

    @GetMapping("/myPage")
    public ModelAndView myPage () {
        return new ModelAndView("html/myPage");
    }

    @GetMapping("/")
    public ModelAndView main () {
        return new ModelAndView("main");
    }

    @GetMapping("/login-page")
    public ModelAndView login () {
        return new ModelAndView("login");
    }

    @GetMapping("/join-page")
    public ModelAndView join () {
        return new ModelAndView("join");
    }

    @GetMapping("/mate/create")
    public ModelAndView createdPost () {
        return new ModelAndView("post/create");
    }

    @GetMapping("/mate/{postId}")
    public ModelAndView postDetail (
            @PathVariable String postId
    ){
        return new ModelAndView("post/detail");
    }

    @GetMapping("/restaurant")
    public ModelAndView restaurant () {
        return new ModelAndView("restaurant");
    }

    @GetMapping("/review/edit")
    public ModelAndView editReview () {
        return new ModelAndView("editReview");
    }

    @GetMapping("/restaurant/detail")
    public ModelAndView restaurantDetail (
            @RequestParam String name,
            @RequestParam String address
    ){
        return new ModelAndView("restaurant");
    }

//    @GetMapping("/mate/{postId}/comment")
//    public ModelAndView comment(
//            @PathVariable String postId
//    ) {
//        return new ModelAndView("post/detail");
//    }
}