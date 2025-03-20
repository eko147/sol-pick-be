package kr.co.solpick.refrigerator.controller;

import kr.co.solpick.refrigerator.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/meal-plan")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;

    /**
     * 사용자의 설문 데이터를 받아 6일치 식단을 추천하는 API
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> getMealPlan(@RequestBody Map<String, Object> request) {
        Map<String, Object> mealPlan = mealPlanService.getMealPlan(request);
        return ResponseEntity.ok(mealPlan);
    }


}
