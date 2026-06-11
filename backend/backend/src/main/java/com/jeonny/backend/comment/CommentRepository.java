package com.jeonny.backend.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<CommentEntity, Long>{

    @Query("""
            SELECT c 
            FROM CommentEntity c
            where c.postId =:postId
            ORDER BY c.createdAt ASC
            """)
    List<CommentEntity> findByPostId(@Param("postId") Long postId);
    
    int countByPostId(Long postId);
}