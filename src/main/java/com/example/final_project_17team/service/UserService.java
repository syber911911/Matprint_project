package com.example.final_project_17team.service;

import com.example.final_project_17team.dto.UserDto;
import com.example.final_project_17team.entity.User;
import com.example.final_project_17team.exception.ErrorCode;
import com.example.final_project_17team.exception.UserException;
import com.example.final_project_17team.jwt.JwtRequestDto;
import com.example.final_project_17team.jwt.JwtTokenDto;
import com.example.final_project_17team.jwt.JwtTokenUtils;
import com.example.final_project_17team.repository.UserRepository;
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

        /*createUser(UserDto.builder()
                .username("user1")
                .password(passwordEncoder.encode("1234"))
                .email("user1@gmail.com")
                .phone("010-0000-0000")
                .gender(true)
                .age(Long.valueOf(20))
                .created_at(LocalDateTime.now())
                .build());*/
    }

    public JwtTokenDto loginUser(JwtRequestDto dto) {
        //UserDto user = this.loadUserByUsername(dto.getUsername());

        Optional<User> optionalUser = userRepository.findByUsername(dto.getUsername());
        if (optionalUser.isEmpty()) throw new UsernameNotFoundException(dto.getUsername());
        User user = optionalUser.get();

        UserDto dto1 = new UserDto();
        dto1.setId(user.getId());
        dto1.setUsername(user.getUsername());
        dto1.setPassword(user.getPassword());
        dto1.setEmail(user.getEmail());
        dto1.setPhone(user.getPhone());
        dto1.setGender(user.isGender());
        dto1.setAge(user.getAge());
        dto1.setImg_url(user.getImg_url());
        dto1.setCreated_at(user.getCreated_at());
        dto1.setModified_at(user.getModified_at());
        log.info("method:loginUser " + dto1.getUsername());
        log.info("method:loginUser " + dto1.getPassword());

        //------------------------------------------------------------------

        if (!passwordEncoder.matches(dto.getPassword(), dto1.getPassword()))
            throw new BadCredentialsException(dto.getUsername());

        JwtTokenDto response = new JwtTokenDto();
        response.setToken(jwtTokenUtils.generateToken(dto1));
        return response;
    }

    public void createUser(UserDto user) {
        if (this.userExists(user.getUsername()))
            throw new UserException(ErrorCode.DUPLICATED_USER_NAME, String.format("Username : ", user.getUsername()));

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
        User user = optionalUser.get();

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setGender(user.isGender());
        dto.setAge(user.getAge());
        dto.setImg_url(user.getImg_url());
        dto.setCreated_at(user.getCreated_at());
        dto.setModified_at(user.getModified_at());
/*                UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.isGender())
                .age(user.getAge())
                .img_url(user.getImg_url())
                .created_at(user.getCreated_at())
                .modified_at(user.getModified_at())
                .build();*/

        log.info("dto " + dto.getUsername());

        return dto;
        // return UserDto.fromEntity(optionalUser.get());
    }

    @Override
    public void createUser(UserDetails user) {

    }
}
