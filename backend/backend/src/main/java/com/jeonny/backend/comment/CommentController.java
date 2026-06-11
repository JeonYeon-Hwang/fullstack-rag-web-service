package com.jeonny.backend.comment;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    
    /* 댓글 등록 */
    @PostMapping(value = "/comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> postCommentApi(
        @RequestBody CommentRequestDto dto
    ){
        Long commentId = commentService.addComment(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Collections.singletonMap("commentId", commentId));
    }

    /* 댓글 목록 불러오기 */
    @GetMapping(value = "/comments/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getComments(
        @PathVariable Long postId
    ){
        return ResponseEntity.ok(commentService.getPosts(postId));
    }
}
