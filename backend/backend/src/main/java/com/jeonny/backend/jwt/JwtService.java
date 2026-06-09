package com.jeonny.backend.jwt;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeonny.backend.util.JWTUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
    
    private final RefreshRepository refreshRepository;

    /* 소셜 로그인 */

    /* Refresh로 Access 다시 발급 */

    /* Refresh 발급 => 저장 */
    @Transactional
    public void addRefresh(String username, String refreshToken){
        RefreshEntity entity = RefreshEntity.builder()
                .username(username)
                .refresh(refreshToken)
                .build();

        refreshRepository.save(entity);
    }


    /* JWT Refresh 유무 확인 */
    @Transactional(readOnly = true)
    public Boolean existByRefresh(String refreshToken){
        return refreshRepository.existsByRefresh(refreshToken);
    }


    /* JWT Refresh 삭제 */
    @Transactional
    public void removeRefresh(String refreshToken){
        refreshRepository.deleteByRefresh(refreshToken);
    }


    /* username기반 refresh 삭제 */
    @Transactional
    public void removeRefreshUser(String username){
        /* 있으면 삭제 */
        refreshRepository.findByUsername(username)
            .ifPresent(entity -> removeRefresh(entity.getRefresh()));

    }

    /* Jwt 토큰을 만드는 실제 서비스 */
    @Transactional
    public Map<String, String> issueToken(String username){
        /* refress, access 각각 발급 */
        String refreshToken = JWTUtil.createJWT(username, false);
        String accessToken = JWTUtil.createJWT(username, true);
    
        /* 기존 refresh를 삭제 => 새 것 db에 저장 */
        removeRefreshUser(username);
        addRefresh(username, refreshToken);

        return Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken
        );
    }
}
