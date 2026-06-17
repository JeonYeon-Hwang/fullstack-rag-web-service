package com.jeonny.backend.newsletter;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NewsletterController {
    
    private final NewsletterService newsletterservice;

    /* 뉴스레터 가져오기 */
    @GetMapping(value = "/newsletter")
    public ResponseEntity<Map<String, Object>> getNewsletterApi(
    ){
        return ResponseEntity.ok(newsletterservice.getNewsletter());
    }


    /* 뉴스레터 쓰기 => aiServer에서 캐치하여 사용 */

    /* 뉴스레터 생성 가능 여부 */
    @GetMapping(value = "/newsletter/perm")
    public ResponseEntity<Long> newsletterAvailApi(){
        return ResponseEntity.ok(newsletterservice.newsletterAvail());
    }
}
