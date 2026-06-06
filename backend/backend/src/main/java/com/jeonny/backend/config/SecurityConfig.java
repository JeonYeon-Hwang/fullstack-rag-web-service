package com.jeonny.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /* Bean에 대한 추가 이해가 필요하다
       신규 객체? 등록 */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /* 필터체인: 매 접속 마다 검수 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        /* 1. csrf 보안 비활성 
           2. 기본 인증 필터 비활성
           3. 로그인 경로는 모두 허용
           4. 예외처리: 401 */
        http.csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, authExp) -> {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                })
                .accessDeniedHandler((req, res, authExp) -> {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                })
            );                 


        return http.build();
    }

}