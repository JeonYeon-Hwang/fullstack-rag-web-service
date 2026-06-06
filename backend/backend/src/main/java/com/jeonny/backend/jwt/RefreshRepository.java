package com.jeonny.backend.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long>{
    
    Boolean existByRefresh(String refreshToken);

    @Transactional
    void deleteByRefresh(String refresh);
}
