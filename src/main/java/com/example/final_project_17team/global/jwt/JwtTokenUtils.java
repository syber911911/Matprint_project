package com.example.final_project_17team.global.jwt;

import com.example.final_project_17team.global.redis.Redis;
import com.example.final_project_17team.global.redis.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class JwtTokenUtils {
    private final Key signingKey;
    private final JwtParser jwtParser;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenUtils(@Value("${jwt.secret}") String jwtSecret, RefreshTokenRepository refreshTokenRepository) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.refreshTokenRepository = refreshTokenRepository;
        // JWT 번역기 만들기
        this.jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(this.signingKey)
                .build();
    }

    // jwt 유효성 검증
    // 헤더가 빈 경우, Bearer 토큰이 아닌 경우 혹은 jwt 를 해석하는 과정에서
    // 예외가 발생하는 경우 false 를 반환
    public void validate(String token) {
        jwtParser.parseClaimsJws(token);
    }

    public String getUsernameFromJwt(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public JwtTokenInfoDto generatedToken(String username) {
        JwtTokenInfoDto jwtTokenInfoDto = new JwtTokenInfoDto();
        String accessToken = this.createAccessToken(username);
        String refreshToken = this.createRefreshToken();
        jwtTokenInfoDto.setAccessToken(accessToken);
        jwtTokenInfoDto.setRefreshToken(refreshToken);
        saveRefreshToken(username, refreshToken);
        return jwtTokenInfoDto;
    }

    public JwtTokenInfoDto regenratedToken(String refreshToken) {
        // 요청으로 보낸 refresh Token 으로 redis 에서 조회
        Optional<Redis> optionalRedis = refreshTokenRepository.findById(refreshToken);
        Optional<Redis> optionalRedis1 = refreshTokenRepository.findRedisByUsername("test1234");
        if (optionalRedis1.isPresent()) {
            System.out.println(optionalRedis1.get().getRefreshToken());
            System.out.println(optionalRedis1.get().getUsername());
        }
        // refresh token 이 조회되지 않으면
        // 로그인 페이지로 리다이렉트
        if (optionalRedis.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "refresh token 이 없다, 로그인 페이지로 가라;");
        // refresh token 이 조회된 경우에는
        // username 추출
        // redis 에서 해당 refresh token 삭제
        Redis redis = optionalRedis.get();
        String username = redis.getUsername();
        refreshTokenRepository.delete(redis);
        // 재발급 진행 후 클라이언트에게 반환
        return this.generatedToken(username);
    }

    // 사용자 정보를 바탕으로 accessToken 발급
    public String createAccessToken(String username) {
        Claims jwtClaims = Jwts.claims()
                // 사용자 정보 등록
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600 * 24)));

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(signingKey)
                .compact();
    }

    // refreshToken 발급
    public String createRefreshToken() {
        return Jwts.builder()
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600 * 24 * 14)))
                .signWith(signingKey)
                .compact();
    }

    public void saveRefreshToken(String username, String refreshToken) {
        refreshTokenRepository.save(new Redis(refreshToken, username));
    }
}