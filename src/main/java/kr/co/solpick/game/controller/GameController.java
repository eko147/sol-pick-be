package kr.co.solpick.game.controller;

import kr.co.solpick.game.dto.*;
import kr.co.solpick.game.entity.GameState;
import kr.co.solpick.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/solpick/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /**
     * 게임 상태 조회 API
     * @param userId 유저 ID
     * @return 게임 상태
     */
    @GetMapping("/state/{userId}")
    public ResponseEntity<GameStateDTO> getGameState(@PathVariable Integer userId) {
        GameState gameState = gameService.getOrCreateGameState(userId);

        GameStateDTO gameStateDTO = GameStateDTO.builder()
                .level(gameState.getLevel())
                .currentExp(gameState.getCurrentExp())
                .energy(gameState.getEnergy())
                .food(gameState.getFood())
                .ingredientsCount(gameState.getIngredientsCount())
                .build();

        return ResponseEntity.ok(gameStateDTO);
    }

    /**
     * 게임 상태 업데이트 API
     * @param userId 유저 ID
     * @param gameStateDTO 업데이트할 게임 상태
     * @return 업데이트된 게임 상태
     */
    @PutMapping("/state/{userId}")
    public ResponseEntity<GameStateDTO> updateGameState(
            @PathVariable Integer userId,
            @RequestBody GameStateDTO gameStateDTO) {

        GameState updatedState = gameService.updateGameState(userId, gameStateDTO);

        GameStateDTO responseDTO = GameStateDTO.builder()
                .level(updatedState.getLevel())
                .currentExp(updatedState.getCurrentExp())
                .energy(updatedState.getEnergy())
                .food(updatedState.getFood())
                .ingredientsCount(updatedState.getIngredientsCount())
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 식재료 발견 정보 초기화 API
     * @param request 초기화 요청 DTO
     * @return 응답 엔티티
     */
    @PostMapping("/initialize-ingredients")
    public ResponseEntity<?> initializeIngredients(
            @RequestBody InitializeIngredientsRequestDTO request) {

        gameService.initializeGameDiscoveredIngredients(
                request.getUserId(),
                request.getRecipeId(),
                request.getIngredients());

        return ResponseEntity.ok().build();
    }

    /**
     * 식재료 발견 처리 API
     * @param request 식재료 발견 요청 DTO
     * @return 발견 결과
     */
    @PostMapping("/discover-ingredient")
    public ResponseEntity<DiscoveryResultDTO> discoverIngredient(
            @RequestBody DiscoverIngredientRequestDTO request) {

        DiscoveryResultDTO result = gameService.discoverIngredient(
                request.getUserId(),
                request.getRecipeId(),
                request.getIngredientName(),
                request.getRecipePoints());

        return ResponseEntity.ok(result);
    }

    /**
     * 사료 추가 API
     * @param userId 유저 ID
     * @param request 사료 수량을 포함한 요청 맵
     * @return 응답 엔티티
     */
    @PutMapping("/add-food/{userId}")
    public ResponseEntity<?> addFood(
            @PathVariable Integer userId,
            @RequestBody Map<String, Integer> request) {

        int amount = request.get("amount");
        gameService.addFood(userId, amount);

        return ResponseEntity.ok().build();
    }

    /**
     * 발견한 식재료 목록 조회 API
     * @param userId 유저 ID
     * @param recipeId 레시피 ID
     * @return 발견한 식재료 목록
     */
    @GetMapping("/discovered-ingredients/{userId}/{recipeId}")
    public ResponseEntity<List<DiscoveredIngredientsDTO>> getDiscoveredIngredients(
            @PathVariable Integer userId,
            @PathVariable Integer recipeId) {

        List<DiscoveredIngredientsDTO> ingredients =
                gameService.getDiscoveredIngredients(userId, recipeId);

        return ResponseEntity.ok(ingredients);
    }
}