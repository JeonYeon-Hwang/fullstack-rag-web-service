package com.jeonny.backend.newsletter;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterRepository extends JpaRepository<NewsletterEntity, Long> {

    Optional<NewsletterEntity> findByUserId(Long userId);

  
} 