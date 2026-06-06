package com.jeonny.backend.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    
    private String username;
    private String password;
    private String nickname;
}
