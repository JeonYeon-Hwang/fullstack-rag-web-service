package com.jeonny.backend.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTfilter extends OncePerRequestFilter{
    
    /* 덮어쓰기 */
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        /* 파싱하기 */
        String authorization = request.getHeader("Authorization");
        if(authorization == null){
            filterChain.doFilter(request, response);
            return;
        }

        /* 형싱 맞는지 검증 */
        if(!authorization.startsWith("Bearer ")){
            throw new ServletException("Invlid JWT token");
        }

        /* 토큰 파싱 */
        String accessToken = authorization.split(" ")[1];

        
        filterChain.doFilter(request, response);
        
    }
}
