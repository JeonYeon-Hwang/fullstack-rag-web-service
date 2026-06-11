package com.jeonny.backend.jwt;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JwtController {
    
    private final JwtService jwtService;

    /* Refresh 기준 Access 재발급  */
    @PostMapping(value = "/jwt/exchange", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> jwtExchangeApi(
        @RequestBody Map<String, String> body
    ){
        String refreshToken = body.get("refreshToken");
        String newAccessToken = jwtService.newAccessToken(refreshToken);

        return ResponseEntity.ok(Map.of(
            "accessToken", newAccessToken
        ));
    }
}
