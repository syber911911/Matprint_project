package com.example.final_project_17team.user.service;

import com.example.final_project_17team.global.dto.ResponseDto;
import com.example.final_project_17team.global.jwt.JwtTokenInfoDto;
import com.example.final_project_17team.global.jwt.JwtTokenUtils;
import com.example.final_project_17team.global.redis.Redis;
import com.example.final_project_17team.global.redis.RedisRepository;
import com.example.final_project_17team.user.dto.CustomUserDetails;
import com.example.final_project_17team.user.dto.LoginDto;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsManager {
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    public UserService(UserRepository userRepository, RedisRepository redisRepository, PasswordEncoder passwordEncoder, JwtTokenUtils jwtTokenUtils) {
        this.userRepository = userRepository;
        this.redisRepository = redisRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public void setRefreshCookie(String refreshToken, String autoLogin, HttpServletResponse response) {
        ResponseCookie refreshTokenCookie = null;
        if (autoLogin.equals("T")) {
            refreshTokenCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Lax")
                    .domain("localhost")
                    .path("/")
                    .maxAge(3600 * 24 * 14)
                    .build();
        } else {
            refreshTokenCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Lax")
                    .domain("localhost")
                    .path("/")
                    .build();
        }
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }

    public void setAutoLoginCookie(String autoLogin, HttpServletResponse response) {
        ResponseCookie autoLoginCookie = null;
        if (autoLogin.equals("T")) {
            autoLoginCookie = ResponseCookie.from("AUTO_LOGIN","T")
                    .sameSite("Lax")
                    .domain("localhost")
                    .path("/")
                    .maxAge(3600 * 24 * 14)
                    .build();
        } else {
            autoLoginCookie = ResponseCookie.from("AUTO_LOGIN", "F")
                    .sameSite("Lax")
                    .domain("localhost")
                    .path("/")
                    .build();
        }
        response.addHeader("Set-Cookie", autoLoginCookie.toString());
    }

    public JwtTokenInfoDto loginUser(LoginDto request) {
        CustomUserDetails user = this.loadUserByUsername(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");

        if (redisRepository.existsById(user.getUsername()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 로그인 된 기기가 존재함");

        return jwtTokenUtils.generateToken(user.getUsername());
    }
    // case 1
    // 1. client -> 서버 : 로그인 요청 (다른 기기에 로그인 된 상태)
    // 2. 서버 -> client : 이미 로그인 되어있다 신호를 보냄 (client 로그아웃 하겠냐?)
    // 3. client -> 서버 : 로그아웃 하겠다
    // 4. 서버 -> client : 로그아웃 되었다 신호를 보냄
    // 5. client -> 서버 : 로그인 요청을 보냄 (기존 유저 정보를 가지고)

    // case 2
    // 1. client -> 서버 : 로그인 요청
    // 2. 서버 : refresh token 갱신
    // 3. 서버 -> client : tokenDto 에 로그아웃 되었음을 함께 표시

    public ResponseDto logout(String username) {
        System.out.println(redisRepository.existsById(username));
        Optional<Redis> optionalRedis = redisRepository.findById(username);
        if (optionalRedis.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "로그아웃 된 계정 혹은 알 수 없는 이유로 로그인 이력을 확인 할 수 없음");
        redisRepository.delete(optionalRedis.get());
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("로그아웃이 완료되었습니다.");
        responseDto.setStatus(HttpStatus.OK);
        return responseDto;
    }

    @Override
    public void createUser(UserDetails user) {
        CustomUserDetails customUserDetails = (CustomUserDetails) user;
        if (this.userExists(customUserDetails.getUsername()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("%s 는 이미 사용중인 아이디 입니다.", customUserDetails.getUsername()));
        if (userRepository.existsByEmail(customUserDetails.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("%s 는 이미 사용중인 이메일 입니다.", customUserDetails.getEmail()));
        if (userRepository.existsByPhone(customUserDetails.getPhone()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("%s 는 이미 사용중인 전화번호 입니다.", customUserDetails.getPhone()));
        try {
            customUserDetails.setEncodedPassword(passwordEncoder.encode(customUserDetails.getPassword()));
            this.userRepository.save(User.fromUserDetails(customUserDetails));
        } catch (ClassCastException e) {
            log.error("Exception message : {} | failed to cast to {}",e.getMessage(), CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다.");
        return CustomUserDetails.fromEntity(optionalUser.get());
    }
}
