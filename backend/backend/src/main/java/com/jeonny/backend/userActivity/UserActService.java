package com.jeonny.backend.userActivity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserActService {
    
    private final UserActRepository userActRepository;

    @Async
    @EventListener
    @Transactional
    public void handlePostViewEvent(PostViewEvent event){
        UserActEntity entity = UserActEntity.builder()
                .userId(event.userId())
                .postId(event.postId())
                .activityType("VIEW")
                .createdAt(LocalDateTime.now())
                .build();

        userActRepository.save(entity);
    }

    @Async
    @EventListener
    @Transactional
    public void handlePostCreateEvent(PostCreateEvent event){
        UserActEntity entity = UserActEntity.builder()
                .userId(event.userId())
                .postId(event.postId())
                .activityType("CREATE")
                .createdAt(LocalDateTime.now())
                .build();

        userActRepository.save(entity);        
    }

    @Async
    @EventListener
    @Transactional
    public void handleCommentCreateEvent(CommentCreateEvent event){
        UserActEntity entity = UserActEntity.builder()
                .userId(event.userId())
                .postId(event.postId())
                .activityType("COMMENT")
                .createdAt(LocalDateTime.now())
                .build();

        userActRepository.save(entity);        
    }


    /* 컨트롤러 호출 로직 */
    @Transactional
    public List<UserActResponseDto> getUserActivity(Long userId){
        List<UserActEntity> entities = userActRepository.findAllByUserId(userId);

        return entities.stream()
                .map(UserActResponseDto::from)
                .collect(Collectors.toList());
    }
}
