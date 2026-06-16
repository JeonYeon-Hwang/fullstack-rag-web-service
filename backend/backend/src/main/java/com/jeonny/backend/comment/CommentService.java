package com.jeonny.backend.comment;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jeonny.backend.user.UserEntity;
import com.jeonny.backend.user.UserRepository;
import com.jeonny.backend.userActivity.CommentCreateEvent;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher eventPublisher;

    /* 댓글 등록하기 */
    @Transactional
    public Long addComment(CommentRequestDto dto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        UserEntity user_entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        CommentEntity comment_entity = CommentEntity.builder()
                .userId(user_entity.getId())
                .postId(dto.getPostId())
                .comment(dto.getComment())
                .build();
        
        /* 댓글 쓰기 이벤트 생성 */
        eventPublisher.publishEvent(new CommentCreateEvent(user_entity.getId(), dto.getPostId()));

        return commentRepository.save(comment_entity).getId();
    }


    /* 댓글 목록 불러오기 */
    @Transactional
    public List<CommentResponseDto> getPosts(Long postId){
        List<CommentEntity> comment_entities = commentRepository.findByPostId(postId);
        
        /* 빈 list 객체를 만들고 하나하나 넣는다 */
        List<CommentResponseDto> result = new ArrayList<>();
        for(CommentEntity comment : comment_entities){
            UserEntity user = userRepository.findById(comment.getUserId()).orElseThrow();
            CommentResponseDto dto = CommentResponseDto.from(comment, user);

            result.add(dto);
        }

        return result;
    }
}
