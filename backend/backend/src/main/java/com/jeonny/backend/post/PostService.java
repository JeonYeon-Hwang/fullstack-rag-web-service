package com.jeonny.backend.post;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jeonny.backend.comment.CommentRepository;
import com.jeonny.backend.user.UserEntity;
import com.jeonny.backend.user.UserRepository;
import com.jeonny.backend.userActivity.PostCreateEvent;
import com.jeonny.backend.userActivity.PostViewEvent;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher eventPublisher;

    /* 글 추가하기 */
    @Transactional
    public Long addPost(PostRequestDto dto){
        /* SecurityContext에서 username => 이걸로 id도 가져오기 */
        UserEntity curr_user = getCurrentUser();
        PostEntity post_entity = PostEntity.builder()
                .userId(curr_user.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .tags(dto.getTags())
                .build();

        Long postId = postRepository.save(post_entity).getId();
        /* 쓰기 이벤트 발행 */
        eventPublisher.publishEvent(new PostCreateEvent(curr_user.getId(), postId));

        return postId;
    }


    /* 글 삭제하기 */


    /* 글 페이지 단위로 */
    @Transactional
    public List<PostResponseDto> getPosts(int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        Page<PostEntity> post_entity = postRepository.findAllByOrderByCreatedAtDesc(pageable);

        return post_entity.stream()
                .map(post -> {
                    UserEntity user = userRepository.findById(post.getUserId()).orElseThrow();
                    Integer comment_num = commentRepository.countByPostId(post.getId());
                    return PostResponseDto.from(post, user, comment_num);
                })
                .toList();
    }


    /* 글 하나 보여주기 */
    @Transactional
    public PostResponseDto getPost(Long postId){
        PostEntity post_entity = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        UserEntity user_entity = userRepository.findById(post_entity.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        /* 조회 이벤트 발행하기 */
        UserEntity curr_user = getCurrentUser();
        if(curr_user != null){
            eventPublisher.publishEvent(new PostViewEvent(curr_user.getId(), postId));
        }
        
        return PostResponseDto.from(post_entity, user_entity, null);
    }


    /* 글 하나 수정하기 */
    @Transactional
    public Boolean updatePost(Long postId, PostRequestDto dto){
        UserEntity user_entity = getCurrentUser();
        if(user_entity == null) throw new EntityNotFoundException("현 유저를 찾을 수 없습니다.");

        PostEntity post_entity = getCurrentPost(postId);
        
        /* 추가 검증: 유저는 해당 글 작성자인가? */
        if(!isPostOwner(post_entity, user_entity)){
            return false;
        }

        /* DB에 반영하기: entity를 set하는 것 만으로 충분 */
        post_entity.setTitle(dto.getTitle());
        post_entity.setContent(dto.getContent());
        post_entity.setTags(
            Arrays.stream(dto.getTags().split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isBlank())
                    .toList()
        );

        return true;
    }

    /* 글 하나 권한 확인하기 */
    @Transactional
    public PostResponseDto getPostPerm(Long postId){
        UserEntity user = getCurrentUser();
        if(user == null) throw new EntityNotFoundException("현 유저를 찾을 수 없습니다.");

        PostEntity post = getCurrentPost(postId);
        if(!isPostOwner(post, user)) return null;

        return PostResponseDto.from(post, user, null);
    }


    /* 글 갯수 가져오기 */
    @Transactional
    public Integer getPostNum(){
        return (int) postRepository.count();
    }



    /* helper 함수들 입니다 */
    private UserEntity getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        /* 비 회원일 경우의 분기 생성 */
        if(auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())){
            return null;
        }
        String username = auth.getName();       
        return userRepository.findByUsername(username).orElse(null);
    }

    private PostEntity getCurrentPost(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));   
    }

    private Boolean isPostOwner(PostEntity postEntity, UserEntity userEntity){
        return postEntity.getUserId().equals(userEntity.getId());
    }
}
