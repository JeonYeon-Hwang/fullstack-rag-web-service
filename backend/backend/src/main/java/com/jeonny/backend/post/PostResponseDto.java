package com.jeonny.backend.post;

import java.time.LocalDateTime;
import java.util.List;

import com.jeonny.backend.user.UserEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponseDto {
    
    private Long postId;
    private String nickname;
    private String title;
    private String content;
    private List<String> tags;
    private LocalDateTime createdAt;

    public static PostResponseDto from(PostEntity post, UserEntity user){
        return PostResponseDto.builder()
                .postId(post.getId())
                .nickname(user.getNickname())
                .title(post.getTitle())
                .content(post.getContent())
                .tags(post.getTags())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
