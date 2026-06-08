package com.jeonny.backend.jwt;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long>{
    
    Boolean existsByRefresh(String refreshToken);

    @Transactional
    void deleteByRefresh(String refresh);

    void deleteByCreatedDateBefore(LocalDateTime cutoff);

    Optional<RefreshEntity> findByUsername(String username);
}
