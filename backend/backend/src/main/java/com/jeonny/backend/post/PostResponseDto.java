package com.jeonny.backend.post;

import java.time.LocalDateTime;

import com.jeonny.backend.user.UserEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponseDto {
    
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static PostResponseDto from(PostEntity post, UserEntity user){
        return PostResponseDto.builder()
                .nickname(user.getNickname())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
