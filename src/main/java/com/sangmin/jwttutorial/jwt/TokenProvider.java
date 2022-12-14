package com.sangmin.jwttutorial.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 토큰의 생성, 토큰의 유효성 검증등을 담당할 TokenProvider
 */
@Component
public class TokenProvider implements InitializingBean {

    private static final String COMMA = ",";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String UNSUPPORTED_JWT_TOKEN = "지원되지 않는 JWT 토큰입니다.";
    private static final String EXPIRED_JWT_TOKEN = "만료된 JWT 토큰입니다.";
    private static final String INVALID_JWT_TOKEN = "잘못된 JWT 서명입니다.";
    private static final String ILLEAGAL_ARGUMENT = "JWT 토큰이 잘못되었습니다.";
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.token-validity-in-seconds}")
    private long tokenValidityInSeconds;

    private Key key;


    /**
     * InitializingBean을 implements하고 afterPropertiesSet를 오버라이딩 한 이유
     * 빈이 생성되고 주입을 받은 후에 secret 값을 Base64 Decode하여 key 변수에 할당하기 위함
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Authentication 객체의 권한 정보를 이용하여 토큰을 생성하는 메소드
     * @param authentication Authentication 객체
     * @return JWT 토큰
     */
    public String createToken(Authentication authentication) {
        // 권한
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 토큰 유효 시간 
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInSeconds);

        return Jwts.builder()
                .setSubject(authentication.getName()) // 토큰 제목
                .claim(AUTHORITIES_KEY, authorities)  // 권한 정보
                .signWith(key, SignatureAlgorithm.HS512) // 암호화 알고리즘
                .setExpiration(validity) // 만료시간
                .compact();
    }


    /**
     * Token에 담겨있는 정보를 이용하여 Authentication 객체를 리턴하는 메소드
     * @param token 토큰
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        // 토큰을 통해 클레임 생성
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 클레임에서 권한 정보 조회
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(COMMA))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        // 유저 객체 생성
        User principal = new User(claims.getSubject(), "", authorities);

        // 유저, 토큰, 권한을 합쳐 Authentication 객체를 생성
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 토큰 유효성 검증
     * @param token 토큰
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info(INVALID_JWT_TOKEN);
        } catch (ExpiredJwtException e) {
            logger.info(EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            logger.info(UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            logger.info(ILLEAGAL_ARGUMENT);
        }
        return false;
    }

}
