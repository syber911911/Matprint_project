package com.example.final_project_17team.user.service;

import com.example.final_project_17team.global.exception.ErrorCode;
import com.example.final_project_17team.global.exception.CustomException;
import com.example.final_project_17team.global.jwt.JwtRequestDto;
import com.example.final_project_17team.global.jwt.JwtTokenDto;
import com.example.final_project_17team.global.jwt.JwtTokenUtils;
import com.example.final_project_17team.user.dto.UserDto;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsManager {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtils jwtTokenUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;

        createUser(UserDto.builder()
                .username("user1")
                .password(passwordEncoder.encode("1234"))
                .email("user1@gmail.com")
                .phone("010-0000-0000")
                .gender(true)
                .age(Long.valueOf(20))
                .created_at(LocalDateTime.now())
                .build());
    }

    public JwtTokenDto loginUser(JwtRequestDto dto) {
        UserDto user = this.loadUserByUsername(dto.getUsername());
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new BadCredentialsException(dto.getUsername());

        JwtTokenDto response = new JwtTokenDto();
        response.setToken(jwtTokenUtils.generateToken(user));
        return response;
    }

    public void createUser(UserDto user) {
        if (this.userExists(user.getUsername()))
            throw new CustomException(ErrorCode.DUPLICATED_USER_NAME, String.format("Username : ", user.getUsername()));

        try {
            this.userRepository.save(user.newEntity());
        } catch (ClassCastException e) {
            log.error("failed to cast to {}", UserDto.class);
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
        return false;
    }

    @Override
    public UserDto loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) throw new UsernameNotFoundException(username);
        return UserDto.fromEntity(optionalUser.get());
    }

    @Override
    public void createUser(UserDetails user) {

    }
}
