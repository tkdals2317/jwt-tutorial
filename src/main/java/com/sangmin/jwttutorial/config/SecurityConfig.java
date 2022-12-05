package com.sangmin.jwttutorial.config;

import com.sangmin.jwttutorial.jwt.JwtAccessDeniedHandler;
import com.sangmin.jwttutorial.jwt.JwtAuthenticationEntryPoint;
import com.sangmin.jwttutorial.jwt.JwtSecurityConfig;
import com.sangmin.jwttutorial.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize 어노테이션을 메소드 단위로 추가하기 위해 적용
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * passwordEncoder는 BCryptPasswordEncoder를 사용
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**"
                        , "/favicon.ico"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 토큰 방식을 사용하기 때문에 csrf()는 disable
                .csrf().disable()
                //Exception Handling을 위해 직접 만든 jwtAuthenticationEntryPoint와 jwtAccessDeniedHandler 등록
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                // H2 Console을 위한 설정
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()
                // 세션 방식이 아닌 토큰 방식을 사용하므로 세션 설정을 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // HttpServletRequest를 사용하는 요청들에 대한 접근 제한 설정
                .and()
                .authorizeRequests()
                .antMatchers("/api/hello").permitAll()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/signup").permitAll()
                .anyRequest().authenticated()
                // JwtSecurityCofig 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));

    }

}
