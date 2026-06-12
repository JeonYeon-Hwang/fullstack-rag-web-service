package com.jeonny.backend.recommend;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.jeonny.backend.post.PostResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${PYTHON_AI_SERVER_URL}")
    private String pythonAiServerUrl;
    
    /* 작성 글 발송 로직 */
    @PostMapping
    public ResponseEntity<List<PostResponseDto>> getRecommendation(
        @RequestBody Map<String, String> requesBody
    ){
        try{
            String targetUrl = pythonAiServerUrl + "/recommend";

            /* 직접 template로 전달 후 응답을 받음: []로 퉁치기 */
            ResponseEntity<PostResponseDto[]> response = restTemplate.postForEntity(
                    targetUrl, 
                    requesBody, 
                    PostResponseDto[].class);   

            /* list 형태로 변환 및 반환 */
            List<PostResponseDto> dtoList = List.of(response.getBody());        
            return ResponseEntity.ok(dtoList);  
        }catch(Exception e){
            return ResponseEntity.status(500).body(null);
        }
    }
}

