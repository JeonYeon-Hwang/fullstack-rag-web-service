package com.jeonny.backend.jwt;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JwtController {
    
    private final JwtService jwtService;

    /* Refresh 기준 Access 재발급  */
    @PostMapping(value = "/jwt/exchange", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> jwtExchangeApi(
        HttpServletRequest request,
        HttpServletResponse response
    ){
        String newAccessToken = "작성해야 함";

        return ResponseEntity.ok(Map.of(
            "accessToken", newAccessToken
        ));
    }
}
