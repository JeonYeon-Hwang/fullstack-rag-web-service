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
import org.springframework.web.bind.annotation.RequestParam;
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


    /* 글 페이지 단위로 보여주기*/
    @GetMapping(value = "/post")
    public ResponseEntity<List<PostResponseDto>> getPostsApi(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ){
        List<PostResponseDto> posts = postService.getPosts(page, size);

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
    public ResponseEntity<Boolean> updatePostApi(
        @RequestBody PostRequestDto dto, @PathVariable Long postId
    ){
        Boolean isUpdated = postService.updatePost(postId, dto);
        return ResponseEntity.ok(isUpdated);
    }

    
    /* 글 하나 권한 확인하기 */
    @GetMapping(value = "/post/perm")
    public ResponseEntity<Map<String, Object>> getPostPermApi(
        @RequestParam Long postId
    ) {
        PostResponseDto post = postService.getPostPerm(postId);
        if(post == null){
            return ResponseEntity.ok(Map.of("permitted", false));
        }

        return ResponseEntity.ok(Map.of(
            "permitted", true,
            "post", post
        ));
    }
    

    /* 총 글 갯수 반환하기 */
    @GetMapping(value = "/post/count")
    public ResponseEntity<Integer> getPostsNumApi(){
        Integer postNum = postService.getPostNum();

        return ResponseEntity.ok(postNum);
    }
}
