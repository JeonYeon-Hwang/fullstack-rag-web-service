package com.jeonny.backend.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JWTUtil {
    private static final SecretKey secretKey;
    private static final Long accessToeknExpiresIn;
    private static final Long refrestToeknExpiresIn;

    /* 실행 시 가장 먼저 작동 */
    static {
        /* 시크릿 키 생성 */
        String secretKeyString = "yeonnyTheHwangIsSomeone";
        secretKey = new SecretKeySpec(
                secretKeyString.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

            /* 만료일 설정: 1시간, 7일 */
            accessToeknExpiresIn = 3600L * 1000;
            refrestToeknExpiresIn = 604800L * 1000;
    }


    /* jwt 발급 */
    public static String createJWT(String username, Boolean isAccess){
        long now = System.currentTimeMillis();
        long expiry = isAccess ? accessToeknExpiresIn : refrestToeknExpiresIn;
        String type = isAccess ? "access" : "refresh";

        return Jwts.builder()
                .claim("sub", username)
                .claim("type", type)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiry))
                .signWith(secretKey)
                .compact();
    }


    /* jwt 검증(validation) */
    public static Boolean isValid(String token, Boolean isAccess){
        try{
            /* 먼저 토큰을 파싱한다 */
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            /* 내부 정보를 바탕으로 유효성 여부를 확인한다 */
            String type = claims.get("type", String.class);
            if(type == null) return false;

            if(isAccess && !type.equals("access")) return false;
            if(!isAccess && type.equals("refresh")) return false;

            return true;
        
        } catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }
}
