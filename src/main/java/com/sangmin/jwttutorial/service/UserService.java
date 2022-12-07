package com.sangmin.jwttutorial.service;

import com.sangmin.jwttutorial.dto.UserDto;
import com.sangmin.jwttutorial.entity.Authority;
import com.sangmin.jwttutorial.entity.User;
import com.sangmin.jwttutorial.repository.UserRepository;
import com.sangmin.jwttutorial.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * @param userDto userDto
     * @return 가입된 회원
     */
    @Transactional
    public UserDto signup(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUserName()).orElse(null) != null){
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        // TODO Authority ENUM 변경
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .username(userDto.getUserName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickName(userDto.getNickName())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return UserDto.from(userRepository.save(user));
    }


    /**
     * username을 기준으로 User 조회
     * @param username username
     * @return User
     */
    @Transactional
    public UserDto getUserWithAuthorities(String username) {
        return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username)
                .orElse(null));
    }

    /**
     * SecurityContext에 저장된 username으로 User 조회
     * @return User
     */
    @Transactional
    public UserDto getMyUserWithAuthorities() {
        return UserDto.from(SecurityUtil.getCurrentUserName()
                .flatMap(userRepository::findOneWithAuthoritiesByUsername)
                .orElse(null));
    }

}
