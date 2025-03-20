package kr.co.solpick.refrigerator.controller;

import kr.co.solpick.refrigerator.entity.RefrigeratorIng;
import kr.co.solpick.refrigerator.service.RefrigeratorIngService;
import kr.co.solpick.refrigerator.service.OpenAiService;
import kr.co.solpick.refrigerator.service.UserAllergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/refrigerator")
@CrossOrigin(origins = "http://localhost:3000") // React와 CORS 허용
public class RefrigeratorIngController {

    @Autowired
    private RefrigeratorIngService refrigeratorIngService;

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private UserAllergyService userAllergyService;

    // 전체 식재료 목록 반환
    @GetMapping("/ingredients")
    public List<RefrigeratorIng> getAllIngredients() {
        return refrigeratorIngService.getAllIngredients();
    }

    // 특정 사용자(userId)의 식재료 목록 반환
    @GetMapping("/ingredients/{userId}")
    public List<RefrigeratorIng> getUserIngredients(@PathVariable Integer userId) {
        return refrigeratorIngService.getIngredientsByUserId(userId);
    }

    // 특정 사용자의 식재료를 기반으로 레시피 추천

    @GetMapping("/recommend/{userId}")
    public Map<String, Object> getRecipeRecommendation(@PathVariable Integer userId) {
        // ✅ 해당 유저의 식재료 목록 가져오기
        List<RefrigeratorIng> ingredients = refrigeratorIngService.getIngredientsByUserId(userId);

        // ✅ 식재료 이름만 추출
        List<String> ingredientNames = ingredients.stream()
                .map(RefrigeratorIng::getName)
                .collect(Collectors.toList());

        // ✅ 사용자의 알러지 정보 가져오기
        List<String> allergies = userAllergyService.getUserAllergies(userId);

        // ✅ OpenAI API 호출하여 레시피 추천 받기 (알러지 목록 포함)
        return openAiService.getRecipeRecommendation(ingredientNames, allergies);
    }

}