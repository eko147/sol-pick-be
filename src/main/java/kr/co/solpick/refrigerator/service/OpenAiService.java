package kr.co.solpick.refrigerator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> getRecipeRecommendation(List<String> ingredients, List<String> allergies) {
        RestTemplate restTemplate = new RestTemplate();

        String allergyFilter = allergies.isEmpty() ? "" : "단, 알러지 재료 [" + String.join(", ", allergies) + "] 포함된 요리는 추천하지 마.";
        String prompt = "식재료 [" + String.join(", ", ingredients) + "] 를 사용하여 만들 수 있는 요리 6개를 JSON 형식으로 추천해줘. "
                + "각 요리는 다음 정보를 포함해야 해:"
                + "- 'name': 요리 이름"
                + "- 'ingredients': 필요한 재료 목록 (배열)"
                + "- 'cooking_time': 조리 시간 (예: '30분')"
                + "- 'difficulty': 난이도 (예: '초급', '중급', '고급')"
                + "- 'steps': 조리 과정 (배열, 단계별 설명 포함)"
                + allergyFilter;
        // ✅ 요청 데이터 설정

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.7,
                "max_tokens", 5000,
                "response_format", Map.of("type", "json_object")
        );
        System.out.println("🔹 OpenAI 요청 데이터: " + requestBody);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // ✅ OpenAI API 호출
        ResponseEntity<String> response = restTemplate.exchange(OPENAI_URL, HttpMethod.POST, entity, String.class);

        try {
            // ✅ JSON 응답을 파싱
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode choicesNode = rootNode.path("choices");

            if (!choicesNode.isArray() || choicesNode.isEmpty()) {
                throw new RuntimeException("OpenAI 응답이 올바르지 않습니다.");
            }

            // ✅ OpenAI가 생성한 JSON 콘텐츠 추출
            JsonNode messageNode = choicesNode.get(0).path("message").path("content");
            Map<String, Object> recipeData = objectMapper.readValue(messageNode.asText(), Map.class);

            return recipeData;

        } catch (Exception e) {
            throw new RuntimeException("응답 파싱 중 오류 발생", e);
        }
    }




}
