package com.sangmin.jwttutorial.util;

import com.sangmin.jwttutorial.jwt.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
    private static final String NOT_EXIST_AUTHENTICATION_IN_SECURITY_CONTEXT = "Security Context에 인증 정보가 없습니다";

    /**
     * Security Context에서 Authentication 객체를 꺼내와 username을 return 해주는 메소드
     * @return username
     */
    public static Optional<String> getCurrentUserName() {
        // JwtFilter의 doFilter 메소드에서 Requset가 들어왔을 때 저장된 객체
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            logger.debug(NOT_EXIST_AUTHENTICATION_IN_SECURITY_CONTEXT);
            return Optional.empty();
        }

        String username = null;

        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }




}
