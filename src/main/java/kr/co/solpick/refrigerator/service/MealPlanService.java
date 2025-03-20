package kr.co.solpick.refrigerator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MealPlanService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> getMealPlan(Map<String, Object> userData) {
        RestTemplate restTemplate = new RestTemplate();

        // ✅ OpenAI 프롬프트 구성
        String prompt = createPrompt(userData);

        // ✅ 요청 데이터 설정
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.7,
                "max_tokens", 10000,
                "response_format", Map.of("type", "json_object")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // ✅ OpenAI API 호출
        ResponseEntity<String> response = restTemplate.exchange(OPENAI_URL, HttpMethod.POST, entity, String.class);
        System.out.println("✅ OpenAI 응답: " + response.getBody());

        try {
            // ✅ JSON 응답을 파싱
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode choicesNode = rootNode.path("choices");

            if (!choicesNode.isArray() || choicesNode.isEmpty()) {
                throw new RuntimeException("OpenAI 응답이 올바르지 않습니다.");
            }

            // ✅ OpenAI가 생성한 JSON 콘텐츠 추출
            JsonNode messageNode = choicesNode.get(0).path("message").path("content");
            Map<String, Object> mealPlan = objectMapper.readValue(messageNode.asText(), Map.class);

            return mealPlan;

        } catch (Exception e) {
            throw new RuntimeException("응답 파싱 중 오류 발생", e);
        }
    }

    /**
     * ✅ 사용자 입력 데이터를 기반으로 OpenAI 프롬프트 생성
     */
    private String createPrompt(Map<String, Object> userData) {
        String gender = userData.get("gender").toString();
        String height = userData.get("height").toString();
        String weight = userData.get("weight").toString();
        String goal = userData.get("goal").toString();
        String targetWeight = userData.get("targetWeight").toString();
        List<String> meals = (List<String>) userData.get("meals");
        String sleepHours = userData.get("sleepHours").toString();
        String activityLevel = userData.get("activityLevel").toString();
        String exercise = userData.get("exercise").toString();

        String prompt = String.format(
                "사용자의 정보를 기반으로 맞춤형 6일치 식단을 추천해주세요. "
                        + "사용자 정보:\n"
                        + "- 성별: %s\n"
                        + "- 신장: %scm\n"
                        + "- 몸무게: %skg\n"
                        + "- 목표: %s\n"
                        + "- 목표 체중: %skg\n"
                        + "- 추천받을 끼니: %s\n"
                        + "- 평균 수면시간: %s\n"
                        + "- 생활 패턴: %s\n"
                        + "- 운동 여부: %s\n\n"
                        + "각 식단은 사용자가 선택한 끼니(아침/점심/저녁)만 포함합니다. "
                        + "각 식단은 JSON 형식으로 제공되어야 하며, 아래 정보를 포함해야 합니다:\n"
                        + "- 'day': 날짜 (Day 1, Day 2 등)\n"
                        + "- 'meal_type': 아침/점심/저녁\n"
                        + "- 'menu': 메뉴 이름\n"
                        + "- 'ingredients': 필요한 재료 목록 (배열, 예: ['닭가슴살', '양파', '토마토'])\n"
                        + "- 'calories': 대략적인 칼로리 정보\n"
                        + "- 'steps': 조리 과정 설명 (배열, 예: ['닭가슴살을 굽는다', '양파와 토마토를 썬다'])\n\n"
                        + "반환 데이터는 JSON 형식이어야 합니다.",
                gender, height, weight, goal, targetWeight, meals, sleepHours, activityLevel, exercise
        );

        System.out.println("🔎 OpenAI 요청 프롬프트:\n" + prompt);
        return prompt;
    }

}

