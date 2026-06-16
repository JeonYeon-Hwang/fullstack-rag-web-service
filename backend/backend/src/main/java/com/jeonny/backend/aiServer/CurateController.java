package com.jeonny.backend.aiServer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CurateController {
    
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${PYTHON_AI_SERVER_URL}")
    private String pythonAiServerUrl;

    /* 특정 유저 활동기록 발송 로직 */
    @PostMapping("/curate/{userId}")
        public ResponseEntity<Map<String, Object>> getRecommendation(
            @PathVariable Long userId
        ){
        try{
            String targetUrl = pythonAiServerUrl + "/curate";

            ResponseEntity<String> response = restTemplate.postForEntity(
                targetUrl, userId, String.class);

            return ResponseEntity.ok(Map.of(
                "user", response
            ));

        }catch(Exception e){
            return ResponseEntity.status(500).body(null);
        }
    }
}
