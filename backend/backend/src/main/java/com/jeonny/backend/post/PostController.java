package com.jeonny.backend.post;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    
    /* 글 올리기 */
    @PostMapping(value = "/post", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> addPostApi(
        @RequestBody PostRequestDto dto
    ){
        /* 글 등록하기 */
        Long postId = postService.addPost(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Collections.singletonMap("postId", postId));
    }

    
    /* 글 삭제하기 */


    /* 글 전체 보여주기 */
    @GetMapping(value = "/post")
    public ResponseEntity<List<PostResponseDto>> getPostsApi(){
        List<PostResponseDto> posts = postService.getPosts();

        return ResponseEntity.ok(posts);
    }


    /* 글 하나 보여주기 */
    @GetMapping(value = "/post/{postId}")
    public ResponseEntity<PostResponseDto> getPostApi(
        @PathVariable Long postId
    ){
        return ResponseEntity.ok(postService.getPost(postId));
    }


    /* 글 하나 수정하기 */
    @PutMapping(value = "/post/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePostApi(
        @RequestBody PostRequestDto dto
    ){
        return ResponseEntity.noContent().build();
    }
}
