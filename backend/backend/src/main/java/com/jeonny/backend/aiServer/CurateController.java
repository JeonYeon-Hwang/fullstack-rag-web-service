package com.jeonny.backend.aiServer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.jeonny.backend.user.UserEntity;
import com.jeonny.backend.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CurateController {
    
    private final UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${PYTHON_AI_SERVER_URL}")
    private String pythonAiServerUrl;

    /* 특정 유저 활동기록 발송 로직 */
    @GetMapping("/curate")
        public ResponseEntity<Map<String, Object>> getRecommendation(
        ){
        try{
            String targetUrl = pythonAiServerUrl + "/curate";
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            UserEntity user_entity = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(username));

            /* 전달 객체 만들기 */
            Map<String, Object> body = Map.of(
                "user_id", user_entity.getId()
            );
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                targetUrl, body, String.class);

            return ResponseEntity.ok(Map.of(
                "newsletter", response.getBody()
            ));

        }catch(Exception e){
            return ResponseEntity.status(500).body(null);
        }
    }
}
