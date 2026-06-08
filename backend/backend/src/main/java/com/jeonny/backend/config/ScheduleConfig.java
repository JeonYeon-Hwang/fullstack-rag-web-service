package com.jeonny.backend.config;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jeonny.backend.jwt.RefreshRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScheduleConfig {
    private final RefreshRepository refreshRepository;
    
    /* 1주일 주기: Refresh 삭제 */
    @Scheduled(cron = "0 0 3 * * *")
    public void refreshEntitySchedule(){
        LocalDateTime cutoff = LocalDateTime.now().minusDays(8);
        refreshRepository.deleteByCreatedDateBefore(cutoff);
    }
}
