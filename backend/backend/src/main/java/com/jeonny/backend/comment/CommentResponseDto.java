package com.jeonny.backend.comment;

import java.time.LocalDateTime;

import com.jeonny.backend.user.UserEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDto {
    
    private Long commentId;
    private Long userId;
    private String nickname;
    private String comment;
    private LocalDateTime createdAt;

    /* 받은 인자값으로 dto로 만들기 */
    public static CommentResponseDto from(CommentEntity comment, UserEntity user){
        return CommentResponseDto.builder()
                .commentId(comment.getId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
