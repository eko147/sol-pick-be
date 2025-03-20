package kr.co.solpick.game.controller;

import kr.co.solpick.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/solpick/api/game/recipe")
@RequiredArgsConstructor
public class GameUserRecipeController {

    private final GameService gameService;

    /**
     * 레시피 선택 API
     * @param request userId와 recipeId를 포함한 요청 맵
     * @return 응답 엔티티
     */
    @PostMapping("/select")
    public ResponseEntity<?> selectRecipe(@RequestBody Map<String, Integer> request) {
        Integer userId = request.get("userId");
        Integer recipeId = request.get("recipeId");

        gameService.saveGameUserRecipe(userId, recipeId);
        return ResponseEntity.ok().build();
    }

    /**
     * 선택된 레시피 조회 API
     * @param userId 유저 ID
     * @return 선택된 레시피 ID를 포함한 응답 엔티티
     */
    @GetMapping("/selected/{userId}")
    public ResponseEntity<?> getSelectedRecipe(@PathVariable Integer userId) {
        Optional<Integer> recipeId = gameService.getSelectedRecipe(userId);

        if (recipeId.isPresent()) {
            return ResponseEntity.ok(Map.of("recipeId", recipeId.get()));
        }

        return ResponseEntity.noContent().build();
    }
}