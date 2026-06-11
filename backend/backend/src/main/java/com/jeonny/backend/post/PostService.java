package com.jeonny.backend.post;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jeonny.backend.comment.CommentRepository;
import com.jeonny.backend.user.UserEntity;
import com.jeonny.backend.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /* 글 추가하기 */
    @Transactional
    public Long addPost(PostRequestDto dto){
        /* SecurityContext에서 username => 이걸로 id도 가져오기 */
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        UserEntity user_entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        PostEntity post_entity = PostEntity.builder()
                .userId(user_entity.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .tags(dto.getTags())
                .build();

        return postRepository.save(post_entity).getId();
    }


    /* 글 삭제하기 */


    /* 글 전체 보여주기 */
    @Transactional
    public List<PostResponseDto> getPosts(){
        /* 해당 각 글의 작성자 nickname도 보여줘야 함 */
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
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

        return PostResponseDto.from(post_entity, user_entity, null);
    }


    /* 글 하나 수정하기 */
    @Transactional
    public Boolean updatePost(PostRequestDto dto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<UserEntity> user_entity = userRepository.findByUsername(username);
        if(user_entity.isEmpty()) return false;

       
        

        return true;
    }
}
