package com.example.final_project_17team.global.jwt;

import com.example.final_project_17team.global.dto.ResponseDto;
import com.example.final_project_17team.user.dto.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;

    public JwtTokenFilter(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }
//    private static final List<String> WHITELIST = Arrays.stream(new String[]{"/users/login", "/users/join", "/users/reissue"}).toList();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 인증이 필요한 요청
        // 헤더가 있어야 한다
        // 헤더가 없으면 -> 예외 ( 로그인 페이지로 리다이렉트 )
        // 헤더가 있지만 Barer 토큰이 아니다 -> 예외 ( 로그인 페이지로 리다이렉트 )
        // 토큰이 있고 validation 성공 -> 인증 객체 생성 후 다음 필터 실행
        // 토큰이 있고 만료 -> 재발급 과정 실행
        // 토큰이 있고 만료가 아닌 다른 예외 -> 예외 ( 로그인 페이지로 리다이렉트 )
        // 2. 인증이 불필요한 요청
        // 헤더가 비어있다.
        // 인증 객체를 만들지 않고 다음 필터 실행
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.split(" ")[1];
                jwtTokenUtils.validate(token);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(new UsernamePasswordAuthenticationToken(jwtTokenUtils.getUsernameFromJwt(token), token, null));
                SecurityContextHolder.setContext(context);
                log.info("set security context with jwt");
                filterChain.doFilter(request, response);
            } catch (Exception ex) {
                this.setErrorMessage(response, request, ex);
            }
        } else {
            log.info("no authHeader");
            filterChain.doFilter(request, response);
        }
    }

    public void setErrorMessage(HttpServletResponse response, HttpServletRequest request, Exception ex) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> responseBody = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        responseBody.put("path", request.getServletPath());
        responseBody.put("error", HttpStatus.UNAUTHORIZED);
        responseBody.put("message", ex.getMessage());
        responseBody.put("status", HttpStatus.UNAUTHORIZED.value());
        try {
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        } catch (Exception e) {
            log.warn("fail error message convert to json");
        }
    }
}
