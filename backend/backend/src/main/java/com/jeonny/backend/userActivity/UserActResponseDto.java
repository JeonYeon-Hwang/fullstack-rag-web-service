package com.jeonny.backend.userActivity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActResponseDto {
    
    private Long id;
    private Long userId;
    private Long postId;
    private String activityType;
    private LocalDateTime createdAt;

    /* entity => dto 변환 */
    public static UserActResponseDto from(UserActEntity entity){
        return UserActResponseDto.builder()
                .userId(entity.getUserId())
                .postId(entity.getPostId())
                .activityType(entity.getActivityType())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
