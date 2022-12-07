package com.sangmin.jwttutorial.controller;

import com.sangmin.jwttutorial.dto.LoginDto;
import com.sangmin.jwttutorial.dto.TokenDto;
import com.sangmin.jwttutorial.jwt.JwtFilter;
import com.sangmin.jwttutorial.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {
        // authenticationToken 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        
        Authentication authenticate = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken); // authenticate 메소드가 실행이 될 때 CustomUserDetailsService에서 재정의한 loadByUsername 메소드가 실행
        // authenticate 객체를 SecurtityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        // 인증 정보를 기반으로 JWT 토큰 생성
        String jwt = tokenProvider.createToken(authenticate);

        // 응답 헤더에 JWT를 담아서 보냄
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        // 응답 바디에도 JWT를 담아서 보냄
        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }
}
