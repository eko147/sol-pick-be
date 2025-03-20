package kr.co.solpick.game.controller;

import kr.co.solpick.game.dto.CompletedRecipeDTO;
import kr.co.solpick.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solpick/api/game")
@RequiredArgsConstructor
public class CompletedRecipesController {

    private final GameService gameService;

    /**
     * 완성된 레시피 목록 조회 API
     * @param userId 유저 ID
     * @return 완성된 레시피 목록
     */
    @GetMapping("/completed-recipes/{userId}")
    public ResponseEntity<List<CompletedRecipeDTO>> getCompletedRecipes(@PathVariable Integer userId) {
        List<CompletedRecipeDTO> completedRecipes = gameService.getCompletedRecipes(userId);
        return ResponseEntity.ok(completedRecipes);
    }
}