package com.example.final_project_17team.global.jwt;

import com.example.final_project_17team.global.redis.Redis;
import com.example.final_project_17team.global.redis.RedisRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtTokenUtils {
    private final Key signingKey;
    private final JwtParser jwtParser;
    private final RedisRepository redisRepository;

    public JwtTokenUtils(@Value("${jwt.secret}") String jwtSecret, RedisRepository redisRepository) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.redisRepository = redisRepository;
        // JWT 번역기 만들기
        this.jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(this.signingKey)
                .build();
    }

    // access token 유효성 검증
    // 헤더가 빈 경우, Bearer 토큰이 아닌 경우 혹은 jwt 를 해석하는 과정에서
    // 예외가 발생하는 경우 false 를 반환
    public void validate(String token) {
        // Bearer token 이 아닌 경우
//        if (!authHeader.startsWith("Bearer ")) {
//            log.warn("Bearer token 이 아님");
//            throw new Exception("Bearer token 이 아닙니다.");
//        }
        try {
//            String token = authHeader.split(" ")[1];
            jwtParser.parseClaimsJws(token);
        } catch (SignatureException ex) {
            log.error("서명이 유효하지 않음");
            throw new SignatureException("JWT 서명이 유효하지 않습니다.");
        } catch (MalformedJwtException ex) {
            log.error("JWT 의 형식이 올바르지 않음");
            throw new MalformedJwtException("JWT 형식이 올바르지 않습니다.");
        } catch (ExpiredJwtException ex) {
            log.error("JWT 의 유효시간이 만료");
            throw new ExpiredJwtException(ex.getHeader(), ex.getClaims(), "JWT 의 유효시간이 만료되었습니다.");
        } catch (UnsupportedJwtException ex) {
            log.error("지원되지 않는 기능이 사용됨");
            throw new UnsupportedJwtException("지원되지 않는 기능이 사용되었습니다.");
        } catch (IllegalArgumentException ex) {
            log.error("JWT 의 내용이 빈 상태");
            throw new IllegalArgumentException("JWT 의 내용이 빈 상태입니다.");
        }
    }

    public String getUsernameFromJwt(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public JwtTokenInfoDto generateToken(String username) {
        JwtTokenInfoDto jwtTokenInfoDto = new JwtTokenInfoDto();
        String accessToken = this.createAccessToken(username);
        String refreshToken = this.createRefreshToken();
        jwtTokenInfoDto.setAccessToken(accessToken);
        jwtTokenInfoDto.setRefreshToken(refreshToken);
        saveRefreshToken(username, refreshToken);
        return jwtTokenInfoDto;
    }

    public JwtTokenInfoDto regenerateToken(String refreshToken) {
        try {
            this.validate(refreshToken);
        } catch (Exception ex) {
            log.warn("refresh token validation exception : {}", ex.getMessage());
            throw ex;
        }
        // 요청으로 보낸 refresh Token 으로 redis 에서 조회
        Optional<Redis> optionalRedis = redisRepository.findRedisByRefreshToken(refreshToken);
        // refresh token 이 조회되지 않으면
        // 로그인 페이지로 리다이렉트
        if (optionalRedis.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "refresh token 이 없다, 로그인 페이지로 가라;");
        // refresh token 이 조회된 경우에는
        // username 추출
        Redis redis = optionalRedis.get();
        String username = redis.getUsername();
        return this.generateToken(username);
    }

    // 사용자 정보를 바탕으로 accessToken 발급
    public String createAccessToken(String username) {
        Claims jwtClaims = Jwts.claims()
                // 사용자 정보 등록
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600 * 24)));
        //








        // accessToken 만료 기간 수정 : 현재 24시간







        //
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
        // 해당 user 가 기존에 로그인 된 기록이 있으면
        // refresh token 갱신
        // 기록이 없다면
        // refresh token 기록
        Optional<Redis> optionalRedis = redisRepository.findById(username);
        if (optionalRedis.isEmpty())
            redisRepository.save(new Redis(username, refreshToken));
        else {
            Redis redis = optionalRedis.get();
            redis.setRefreshToken(refreshToken);
            redisRepository.save(redis);
        }
    }
}