package com.example.final_project_17team.user.controller;

import com.example.final_project_17team.global.dto.ResponseDto;
import com.example.final_project_17team.global.exception.ErrorCode;
import com.example.final_project_17team.global.exception.CustomException;
import com.example.final_project_17team.global.jwt.JwtTokenInfoDto;
import com.example.final_project_17team.global.jwt.JwtTokenUtils;
import com.example.final_project_17team.post.dto.ReadPostDto;
import com.example.final_project_17team.post.service.PostService;
import com.example.final_project_17team.review.dto.ReadReviewDto;
import com.example.final_project_17team.review.service.ReviewService;
import com.example.final_project_17team.user.dto.*;
import com.example.final_project_17team.user.service.UserService;
import com.example.final_project_17team.wishlist.dto.WishlistDto;
import com.example.final_project_17team.wishlist.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final WishlistService wishlistService;
    private final PostService postService;
    private final ReviewService reviewService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/login")
    // login 시도 시 redis 에서 해당 유저의 refresh token 이 있는지 확인
    // 1. login logic 에서 기존 refresh token 을 지우고 새로 토큰 발급
    // 2. 기존 refresh token 으로 재발급
    public JwtTokenInfoDto login(@RequestBody @Valid LoginDto request, @RequestParam(value = "autoLogin") String autoLogin, HttpServletResponse response) {
        JwtTokenInfoDto jwtTokenInfoDto = userService.loginUser(request);
        userService.setRefreshCookie(jwtTokenInfoDto.getRefreshToken(), autoLogin, response);
        userService.setAutoLoginCookie(autoLogin, response);
        return jwtTokenInfoDto;
    }

    @PostMapping("/reissue")
    public JwtTokenInfoDto reissue(@CookieValue("REFRESH_TOKEN") String refreshToken, @CookieValue("AUTO_LOGIN") String autoLogin, HttpServletResponse response) {
        JwtTokenInfoDto jwtTokenInfoDto = jwtTokenUtils.regenerateToken(refreshToken);
        userService.setRefreshCookie(jwtTokenInfoDto.getRefreshToken(), autoLogin, response);
        userService.setAutoLoginCookie(autoLogin, response);
        return jwtTokenInfoDto;
    }

    @PostMapping("/join")
    public ResponseDto join(@RequestBody @Valid JoinDto request) {
        if (!request.getPasswordCheck().equals(request.getPassword()))
            throw new CustomException(ErrorCode.DIFF_PASSWORD_CHECK, String.format("Username : %s", request.getUsername()));
        userService.createUser(CustomUserDetails.fromDto(request));

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("회원가입이 완료되었습니다.");
        responseDto.setStatus(HttpStatus.OK);
        return responseDto;
    }

    @PostMapping("/logout")
    public ResponseDto logout(@AuthenticationPrincipal String username, HttpServletRequest request, HttpServletResponse response) {
        log.info(username);
        return userService.logout(username, request, response);
    }

    @GetMapping("/profile/wishlist")
    public List<WishlistDto> readMyWishlist(@AuthenticationPrincipal String username) {
        return wishlistService.readMyWishlist(username);
    }

    @GetMapping("/profile/post")
    public List<ReadPostDto> readMyPost(@AuthenticationPrincipal String username) {
        return postService.readMyPost(username);
    }

    @GetMapping("/profile/review")
    public List<ReadReviewDto> readMyReview(@AuthenticationPrincipal String username) {
        return reviewService.readMyReview(username);
    }

    //회원정보조회
    @GetMapping("/profile")
    public UserProfile getProfile(@AuthenticationPrincipal String username) {
        return userService.readUser(username);
    }

    //회원정보수정
    @PutMapping("/profile")
    public ResponseDto update(@RequestBody UpdateProfileDto updateDto){
        userService.updateUser(CustomUserDetails.fromDto(updateDto));
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("사용자 정보 수정이 완료되었습니다.");
        responseDto.setStatus(HttpStatus.OK);
        return responseDto;
    }

    @PutMapping("/profile/image")
    public ResponseDto uploadImage(MultipartFile multipartFile, @AuthenticationPrincipal String username) {
        return userService.uploadProfileImage(username, multipartFile);
    }

    //회원탈퇴
    @DeleteMapping("/profile")
    public ResponseDto delete(@AuthenticationPrincipal String username) {
        userService.deleteUser(username);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("회원탈퇴가 완료되었습니다.");
        responseDto.setStatus(HttpStatus.OK);
        return responseDto;
    }
}
