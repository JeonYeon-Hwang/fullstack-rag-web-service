package com.jeonny.backend.userActivity;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserActController {

    private final UserActService userActService;

    @GetMapping(value = "/activity/{userId}")
    public ResponseEntity<List<UserActResponseDto>> getUserActApi(
        @PathVariable Long userId
    ){
        return ResponseEntity.ok(userActService.getUserActivity(userId));
    }
    
}
