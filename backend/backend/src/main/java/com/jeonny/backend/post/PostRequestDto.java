package com.jeonny.backend.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
    
    private String title;
    private String content;
    private String tags;
}
