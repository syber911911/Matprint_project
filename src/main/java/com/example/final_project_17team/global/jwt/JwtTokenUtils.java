package com.example.final_project_17team.global.jwt;

import com.example.final_project_17team.global.redis.Redis;
import com.example.final_project_17team.global.redis.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

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
    public boolean validate(String authHeader) {
        if (authHeader == null) {
            log.info("AuthHeader 가 빈 상태");
            return false;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.warn("지원되지 않는 토큰 타입");
            return false;
        }
        try {
            String token = authHeader.split(" ")[1];
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.warn("서명 오류");
            return false;
        } catch (MalformedJwtException ex) {
            log.warn("형식 오류");
            return false;
        } catch (ExpiredJwtException ex) {
            log.warn("유효 시간 만료");
            // 토큰 재발급
            return false;
        } catch (UnsupportedJwtException ex) {
            log.warn("지원되지 않는 기능 사용");
            return false;
        } catch (IllegalArgumentException ex) {
            log.warn("내용이 빈 상태");
            return false;
        }
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
        jwtTokenInfoDto.setLogOut(false);
        saveRefreshToken(username, jwtTokenInfoDto);
        return jwtTokenInfoDto;
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

    public void saveRefreshToken(String username, JwtTokenInfoDto jwtTokenInfoDto) {
        refreshTokenRepository.save(new Redis(username, jwtTokenInfoDto));
    }
}