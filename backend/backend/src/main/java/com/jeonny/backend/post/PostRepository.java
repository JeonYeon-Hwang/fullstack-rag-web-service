package com.jeonny.backend.post;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long>{

    // List<PostEntity> findAllByOrderByCreatedAtDesc();
    
    /* 페이징 기반 반환 */
    Page<PostEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
