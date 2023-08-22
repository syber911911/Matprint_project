package com.example.final_project_17team.jwt;

import com.example.final_project_17team.redis.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;


// 토큰 검증
@Slf4j
public class JwtTokenProvider {
    private final String key;
    private final RedisUtil redisUtil;

    public JwtTokenProvider(String key, RedisUtil redisUtil) {
        this.key = key;
        this.redisUtil = redisUtil;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            // 추가된 부분
            if (redisUtil.hasKeyBlackList(token)){
                // TODO 에러 발생시키는 부분 수정
                throw new RuntimeException("이미 로그아웃한 계정입니다.");
            }
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
