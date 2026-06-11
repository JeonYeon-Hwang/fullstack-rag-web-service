package com.jeonny.backend.user;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    /* 유저 존재 확인 */
    @PostMapping(value = "/user/exist", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> existUserApi(
        @RequestBody UserRequestDto dto
    ){
        return ResponseEntity.ok(userService.existUser(dto));
    }


    /* 회원가입 */
    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> joinApi(
        @RequestBody UserRequestDto dto
    ){
        Long id = userService.addUser(dto);
        Map<String, Long> responseBody = Collections.singletonMap("userEntityId", id);
        return ResponseEntity.status(201).body(responseBody);
    }


    /* 유저 정보 가져오기 */
    @GetMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDto userMeApi(){
        return userService.readUser();
    }

    /* 자체 로그인 */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> loginApi(
        @RequestBody UserRequestDto dto
    ){
        Map<String, String> tokens = userService.login(dto);
        
        return ResponseEntity.ok(tokens);
    }

    /* 로그아웃: refresh 삭제 */
    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logoutApi(
        @RequestBody Map<String, String> body
    ){
        String refreshToken = body.get("refreshToken");
        userService.logout(refreshToken);
        
        return ResponseEntity.noContent().build();
    }
}
