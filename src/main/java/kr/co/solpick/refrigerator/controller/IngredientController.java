package kr.co.solpick.refrigerator.controller;

import kr.co.solpick.refrigerator.dto.IngredientRequestDTO;
import kr.co.solpick.refrigerator.dto.IngredientResponseDTO;
import kr.co.solpick.refrigerator.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solpick/refrigerator/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    // 식재료 등록
    @PostMapping
    public ResponseEntity<IngredientResponseDTO> addIngredient(@RequestBody IngredientRequestDTO requestDto) {
        IngredientResponseDTO responseDto = ingredientService.addIngredient(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 식재료 수정
    @PutMapping("/{ingId}")
    public ResponseEntity<IngredientResponseDTO> updateIngredient(
            @PathVariable Long ingId,
            @RequestBody IngredientRequestDTO requestDto) {
        IngredientResponseDTO responseDto = ingredientService.updateIngredient(ingId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 식재료 삭제
    @DeleteMapping("/{ingId}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long ingId) {
        ingredientService.deleteIngredient(ingId);
        return ResponseEntity.noContent().build();
    }

    // 식재료 목록 조회 (정렬 옵션)
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<IngredientResponseDTO>> getIngredientList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "latest") String sortType
    ) {
        List<IngredientResponseDTO> ingredients = ingredientService.getIngredientList(userId, sortType);
        return ResponseEntity.ok(ingredients);
    }

    // 카테고리별 식재료 목록 조회
    @GetMapping("/list/category/{userId}")
    public ResponseEntity<List<IngredientResponseDTO>> getIngredientsByCategory(
            @PathVariable Long userId,
            @RequestParam(required = false) String mainCategory,
            @RequestParam(required = false) String subCategory,
            @RequestParam(required = false) String detailCategory,
            @RequestParam(defaultValue = "latest") String sortType)
    {
        List<IngredientResponseDTO> ingredients = ingredientService.getIngredientsByCategories(
                userId, mainCategory, subCategory, detailCategory, sortType);
        return ResponseEntity.ok(ingredients);
    }

    // 식재료 상세 정보 조회
    @GetMapping("/detail/{ingId}")
    public ResponseEntity<IngredientResponseDTO> getIngredientDetail(@PathVariable Long ingId) {
        IngredientResponseDTO responseDto = ingredientService.getIngredientDetail(ingId);
        return ResponseEntity.ok(responseDto);
    }
}