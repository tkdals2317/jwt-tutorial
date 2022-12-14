package com.sangmin.jwttutorial.jwt;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * JWT를 위한 커스텀 필터
 */
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {


    private final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final int TOKEN_START_INDEX = 7;

    private final TokenProvider tokenProvider;


    /**
     * 실제 필터링 로직을 위한 GenericFilterBean의 doFilter 메소드 오버라이딩
     * JWT 토큰의 인증 정보를 현재 실행중인 SecurityCotext에 저장하는 역할 수행
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri : {}", authentication.getName(), requestURI);
        } else {
            logger.debug("유효한 JWT 토큰이 없습니다, uri : {}", requestURI);
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Request Header에서 토큰 정보를 꺼내오기 위한 메소드
     * @param request HttpServletRequest
     * @return
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(TOKEN_START_INDEX);
        }
        return null;
    }
}
