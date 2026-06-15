package com.jeonny.backend.userActivity;

import java.time.LocalDateTime;

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
}
