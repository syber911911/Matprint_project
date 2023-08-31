package com.example.final_project_17team.user.controller;

import com.example.final_project_17team.global.dto.ResponseDto;
import com.example.final_project_17team.global.exception.ErrorCode;
import com.example.final_project_17team.global.exception.CustomException;
import com.example.final_project_17team.global.jwt.JwtTokenInfoDto;
import com.example.final_project_17team.global.jwt.JwtTokenUtils;
import com.example.final_project_17team.post.entity.Post;
import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.user.dto.CustomUserDetails;
import com.example.final_project_17team.user.dto.JoinDto;
import com.example.final_project_17team.user.dto.LoginDto;
import com.example.final_project_17team.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserService service;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/login")
    // login 시도 시 redis 에서 해당 유저의 refresh token 이 있는지 확인
    // 1. login logic 에서 기존 refresh token 을 지우고 새로 토큰 발급
    // 2. 기존 refresh token 으로 재발급
    public JwtTokenInfoDto login(@RequestBody @Valid LoginDto request, @RequestParam(value = "autoLogin") String autoLogin, HttpServletResponse response) {
        JwtTokenInfoDto jwtTokenInfoDto = service.loginUser(request);
        service.setRefreshCookie(jwtTokenInfoDto.getRefreshToken(), autoLogin, response);
        service.setAutoLoginCookie(autoLogin, response);
        return jwtTokenInfoDto;
    }

    @PostMapping("/reissue")
    public JwtTokenInfoDto reissue(@CookieValue("REFRESH_TOKEN") String refreshToken, @CookieValue("AUTO_LOGIN") String autoLogin, HttpServletResponse response) {
        JwtTokenInfoDto jwtTokenInfoDto = jwtTokenUtils.regeneratedToken(refreshToken);
        service.setRefreshCookie(jwtTokenInfoDto.getRefreshToken(), autoLogin, response);
        service.setAutoLoginCookie(autoLogin, response);
        return jwtTokenInfoDto;
    }

    @PostMapping("/join")
    public void join(@RequestBody @Valid JoinDto request) {
        if (!request.getPasswordCheck().equals(request.getPassword()))
            throw new CustomException(ErrorCode.DIFF_PASSWORD_CHECK, String.format("Username : %s", request.getUsername()));
        service.createUser(CustomUserDetails.fromDto(request));
    }

    @PostMapping("/logout")
    public ResponseDto logout(@AuthenticationPrincipal String username) {
        log.info(username);
        return service.logout(username);
    }

    @GetMapping("/reviews")
    public List<Review> getReviews(@AuthenticationPrincipal String username){
        log.info(username);
        return service.readReview(username);
    }

    @GetMapping("/post")
    public List<Post> getPost(@AuthenticationPrincipal String username){
        log.info(username);
        return service.readPost(username);
    }

    //회원정보조회
    @GetMapping("/profile")
    public ResponseEntity<CustomUserDetails> getProfile(@AuthenticationPrincipal String username) {
        log.info(username);
        CustomUserDetails customUserDetails = service.readUser(username);
        return ResponseEntity.ok(customUserDetails);
    }

    //회원정보수정
    @PutMapping("/profile")
    public void update(@RequestBody @Valid JoinDto request){
        if (!request.getPasswordCheck().equals(request.getPassword()))
            throw new CustomException(ErrorCode.DIFF_PASSWORD_CHECK, String.format("Username : %s", request.getUsername()));
        service.updateUser(CustomUserDetails.fromDto(request));
    }

    //회원탈퇴
    @DeleteMapping("/profile")
    public void delete(@AuthenticationPrincipal String username) {
        service.deleteUser(username);
    }

}
