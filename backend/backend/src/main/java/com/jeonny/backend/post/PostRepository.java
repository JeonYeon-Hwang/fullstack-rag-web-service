package com.jeonny.backend.post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long>{

    List<PostEntity> findAllByOrderByCreatedAtDesc();
    
}
