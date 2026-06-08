package com.jeonny.backend.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeonny.backend.jwt.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshTokenLogoutHandler implements LogoutHandler{
    
    private final JwtService jwtService;

    /* 덮어쓰기 적용 */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        try{
            String body = StreamUtils.copyToString(
                request.getInputStream(),
                StandardCharsets.UTF_8
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);
            String refreshToken = jsonNode.path("refreshToken").asText(null);
            
            jwtService.removeRefresh(refreshToken);
        } catch (IOException e){
            throw new RuntimeException("Failed to read refresh token", e);
        }
    }
}
