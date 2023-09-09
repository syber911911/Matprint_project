package com.example.final_project_17team.user.service;

import com.example.final_project_17team.global.dto.ResponseDto;
import com.example.final_project_17team.global.jwt.JwtTokenInfoDto;
import com.example.final_project_17team.global.jwt.JwtTokenUtils;
import com.example.final_project_17team.global.redis.Redis;
import com.example.final_project_17team.global.redis.RedisRepository;
import com.example.final_project_17team.post.repository.CommentRepository;
import com.example.final_project_17team.post.repository.PostRepository;
import com.example.final_project_17team.review.repository.ReviewRepository;
import com.example.final_project_17team.user.dto.CustomUserDetails;
import com.example.final_project_17team.user.dto.LoginDto;
import com.example.final_project_17team.user.dto.UserProfile;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import com.example.final_project_17team.wishlist.repository.WishlistRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsManager {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final WishlistRepository wishlistRepository;
    private final PostRepository postRepository;
    private final RedisRepository redisRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

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
            autoLoginCookie = ResponseCookie.from("LOGIN","T")
                    .sameSite("Lax")
                    .domain("localhost")
                    .path("/")
                    .maxAge(3600 * 24 * 14)
                    .build();
        } else {
            autoLoginCookie = ResponseCookie.from("LOGIN", "F")
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

    public UserProfile readUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isPresent())
            return UserProfile.fromEntity(optionalUser.get());
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Override
    public void updateUser(UserDetails user) {
        CustomUserDetails updatedUserDetails = (CustomUserDetails) user;

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        User foundUser = optionalUser.get();

        foundUser.update(updatedUserDetails);
        userRepository.save(foundUser);
    }

    // TODO 사용자 이미지 삭제 기능
    // TODO 사용자 이미지 업로드 시 기존 이미지 삭제
    // TODO 사용자 삭제 시 사용자 ID 로 생성된 디렉토리 삭제
    public ResponseDto uploadProfileImage(String username, MultipartFile profileImage) {
        if (profileImage.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드 할 이미지가 존재하지 않습니다.");
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자가 존재하지 않습니다.");
        User user = optionalUser.get();

        // 사용자 프로필 이미지 저장 디렉토리
        String imageDir = String.format("media/%d/profile/", user.getId());
        try {
            Files.createDirectories(Path.of(imageDir));
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "디렉토리 생성에 실패");
        }
        // 생성 시간
        LocalDateTime createTime = LocalDateTime.now();

        // 첨부된 이미지 filename 추출
        String originalFileName = profileImage.getOriginalFilename();
        // 확장자 추출
        String[] fileNameSplit = originalFileName.split("\\.");
        String extension = fileNameSplit[fileNameSplit.length - 1];
        // 저장할 이미지의 filename 재설정 (생성시간_username.확장자)
        String profileImageFileName = String.format("%s.%s", createTime.toString(), extension);
        // 이미지 저장 경로와 filename 을 합쳐 최종적으로 저장될 path 생성
        String profileImagePath = imageDir + profileImageFileName;

        try {
            // path 에 이미지 저장
            profileImage.transferTo(Path.of(profileImagePath));
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 실패");
        }

        // userEntity 에 이미지 경로 추가 및 저장
        user.updateProfileImage(String.format("/profile/image/%s/%s", username, profileImageFileName));
        userRepository.save(user);

        ResponseDto response = new ResponseDto();
        response.setMessage("프로필 이미지가 업로드 되었습니다");
        response.setStatus(HttpStatus.OK);
        return response;
    }

    @Transactional
    @Override
    public void deleteUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) throw new UsernameNotFoundException(username);
        User user = optionalUser.get();

        //사용자의 리뷰삭제
        reviewRepository.deleteAllByUser(user);
        //사용자의 즐겨찾기 삭제
        wishlistRepository.deleteAllByUser(user);
        postRepository.deleteAllByUser(user);
        commentRepository.deleteAllByUser(user);
        //사용자삭제
        userRepository.deleteById(user.getId());
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
