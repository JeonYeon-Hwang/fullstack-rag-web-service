package com.jeonny.backend.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jeonny.backend.user.UserService;
import com.jeonny.backend.util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTfilter extends OncePerRequestFilter{
    
    private final UserService userService;

    /* 덮어쓰기 */
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        /* 파싱하기 => 초기 검증 */
        String authorization = request.getHeader("Authorization");
        if(authorization == null || !authorization.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        /* 토큰 내용만 뽑기 */
        String accessToken = authorization.split(" ")[1];

        /* 내용 검증 */
        if(!JWTUtil.isValid(accessToken, true)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        /* username 뽑기 => 기반으로 user정보 뽑기 */
        String username = JWTUtil.getUsername(accessToken);
        UserDetails userDetails = userService.loadUserByUsername(username);
        
        /* 인증 객체 만들기 */
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
        
        /* 이후 저장하기  */
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);       
    }
}
