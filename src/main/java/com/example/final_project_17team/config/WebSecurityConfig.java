package com.example.final_project_17team.config;

import com.example.final_project_17team.global.jwt.JwtTokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    public WebSecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .logout(LogoutConfigurer::disable)
                .authorizeHttpRequests(authHttpRequest -> authHttpRequest
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/mate", "/api/mate/{postId}/comment",
                                "/api/{restaurantId}/review", "/api/logout",
                                "/api/wishlist/{restaurantId}"

                        )
                        .authenticated()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/profile/wishlist", "/api/profile/post",
                                "/api/profile/review", "/api/profile",
                                "/api/username"
                        )
                        .authenticated()
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/mate/{postId}", "/api/mate/{postId}/comment/{commentId}",
                                "/api/{restaurantId}/review/{reviewId}", "/api/profile",
                                "/api/profile/image"
                        )
                        .authenticated()
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/mate/{postId}", "/{postId}/comment/{commentId}",
                                "/api/{restaurantId}/review/{reviewId}", "/api/profile"
                        )
                        .authenticated()
                        .anyRequest()
                        .permitAll()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new AuthenticationEntryPoint() {
                            @Override
                            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                response.setCharacterEncoding("UTF-8");
                                Map<String, Object> responseBody = new HashMap<>();
                                ObjectMapper objectMapper = new ObjectMapper();
                                responseBody.put("path", request.getServletPath());
                                responseBody.put("error", HttpStatus.UNAUTHORIZED);
                                responseBody.put("message", "사용자 인증 오류");
                                responseBody.put("status", HttpStatus.UNAUTHORIZED.value());
                                try {
                                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                } catch (Exception e) {
                                    log.warn("fail error message convert to json");
                                }
                            }
                        }));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
