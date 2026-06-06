package com.jeonny.backend.jwt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return refreshRepository.existByRefresh(refreshToken);
    }


    /* JWT Refresh 삭제 */
    @Transactional
    public void removeRefresh(String refreshToekn){
        refreshRepository.deleteByRefresh(refreshToekn);
    }


    /* username기반 refresh 삭제 */
    @Transactional
    public void removeRefreshUser(String username){
        
    }
}
