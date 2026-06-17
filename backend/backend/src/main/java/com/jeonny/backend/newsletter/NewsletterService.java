package com.jeonny.backend.newsletter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeonny.backend.user.UserEntity;
import com.jeonny.backend.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class NewsletterService {
    
    private final NewsletterRepository newsletterRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, Object> getNewsletter(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        UserEntity user_entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));       
        
        NewsletterEntity entity = newsletterRepository.findByUserId(user_entity.getId())
                .orElseThrow(() -> new EntityNotFoundException("뉴스레터를 찾을 수 없습니다"));

        /* map 형태로 조립하여 반환한다 */
        return Map.of(
            "userId", entity.getUserId(),
            "title", entity.getTitle(),
            "metadata", entity.getMetadata(),
            "createdAt", entity.getCreatedAt()
        );
    }


    /* 뉴스레터 저장 메서드 */
    @Transactional
    public Boolean saveNewsletter(NewsletterResponseDto dto, UserEntity user){
        /* entity 세부 설정하기 */
        NewsletterEntity newsletter = new NewsletterEntity();
        newsletter.setUserId(user.getId());
        newsletter.setTitle(dto.title());
        newsletter.setMetadata(dto.metadata());
        newsletter.setCreatedAt(LocalDateTime.now());

        /* 저장 시행: 기존 것이 있으면 삭제 */
        newsletterRepository.findByUserId(user.getId())
                .ifPresent(newsletterRepository::delete);

        newsletterRepository.save(newsletter);
        
        return true;
    }

    /* 뉴스레터 생성 가능여부 확인 메서드 */
    @Transactional
    public Long newsletterAvail(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        UserEntity user_entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

            /* 전달 객체 만들기 */        
        Optional<NewsletterEntity> entity = newsletterRepository.findByUserId(user_entity.getId());
        
        if (entity.isEmpty()) {
            return -1L;
        }


        LocalDateTime createdAt = entity.get().getCreatedAt();
        LocalDateTime expiresAt = createdAt.plusHours(3);
        
        /* 남은 시간 계산 */
        LocalDateTime now = LocalDateTime.now();
        Duration remaining = Duration.between(now, expiresAt);

        return remaining.toMinutes();
    }
}
