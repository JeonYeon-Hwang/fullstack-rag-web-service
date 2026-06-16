package com.jeonny.backend.userActivity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActRepository extends JpaRepository<UserActEntity, Long>{

    List<UserActEntity> findAllByUserId(Long userId);

}
