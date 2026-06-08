package com.jeonny.backend.user;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Boolean existUser(UserRequestDto dto){
        return userRepository.existByUsername(dto.getUsername());
    }


    /* 본인 확인 */
    @Transactional
    public Boolean usernameMatch(UserRequestDto dto){
        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!sessionUsername.equals(dto.getUsername())){
            return true;
        }

        return false;
    }
    

    /* 회원가입 */
    @Transactional
    public Long addUser(UserRequestDto dto) throws AccessDeniedException {
        if(userRepository.existByUsername(dto.getUsername())){
            throw new IllegalArgumentException("이미 유저가 존재합니다.");
        }

        UserEntity entity = UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .build();

        return userRepository.save(entity).getId();
    }


    /* 로그인 */
    @Transactional(readOnly = true)
    // @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.builder()   
                .username(entity.getUsername())
                .password(entity.getPassword())
                .build();
    }


    /* 세션 내 유저 정보 조회 */
    @Transactional(readOnly = true)
    public UserResponseDto readUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다.")); 
    
        return new UserResponseDto(username, entity.getNickname());
    }
}
