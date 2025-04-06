package kr.co.solpick.refrigerator.service;

import kr.co.solpick.refrigerator.dto.IngredientRequestDTO;
import kr.co.solpick.refrigerator.dto.IngredientResponseDTO;
import kr.co.solpick.refrigerator.entity.Ingredient;
import kr.co.solpick.refrigerator.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    // 식재료 등록
    @Transactional
    public IngredientResponseDTO addIngredient(IngredientRequestDTO dto) {
        // createdAt이 전달되지 않았으면, expiryDate에서 7일을 빼서 계산 (주문일)
        LocalDateTime createdTime;

        if (dto.getCreatedAt() != null) {
            createdTime = dto.getCreatedAt(); // 명시적으로 전달된 경우
        } else {
            createdTime = LocalDateTime.now(); // 없으면 현재 시간
        }

        Ingredient ingredient = Ingredient.builder()
                .userId(dto.getUserId())
                .name(dto.getName())
                .emoji(dto.getEmoji())
                .image(dto.getImage())
                .quantity(dto.getQuantity())
                .expiryDate(dto.getExpiryDate())
                .mainCategory(dto.getMainCategory())
                .subCategory(dto.getSubCategory())
                .detailCategory(dto.getDetailCategory())
                .createdAt(createdTime) // createdTime 값 전달
                .updatedAt(LocalDateTime.now())
                .build();

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return IngredientResponseDTO.fromEntity(savedIngredient);
    }

    // 식재료 수정
    @Transactional
    public IngredientResponseDTO updateIngredient(Long ingredientId, IngredientRequestDTO dto) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("해당 식재료가 존재하지 않습니다."));

        ingredient.update(
                dto.getName(),
                dto.getEmoji(),
                dto.getImage(),
                dto.getQuantity(),
                dto.getExpiryDate(),
                dto.getMainCategory(),
                dto.getSubCategory(),
                dto.getDetailCategory()
        );

        return IngredientResponseDTO.fromEntity(ingredient);
    }

    // 식재료 삭제
    @Transactional
    public void deleteIngredient(Long ingredientId) {
        // 존재 여부 확인
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("해당 식재료가 존재하지 않습니다."));

        // 실제 DB에서 삭제
        ingredientRepository.delete(ingredient);
    }

    // 식재료 목록 조회 (정렬 옵션)
    @Transactional(readOnly = true)
    public List<IngredientResponseDTO> getIngredientList(Long userId, String sortType) {
        List<Ingredient> ingredients;
        switch (sortType) {
            case "latest": // 최신순 (기본값)
                ingredients = ingredientRepository.findByUserIdOrderByCreatedAtDesc(userId);
                break;
            case "oldest": // 등록순
                ingredients = ingredientRepository.findByUserIdOrderByCreatedAtAsc(userId);
                break;
            case "expiration": // 유통기한 임박순
                ingredients = ingredientRepository.findByUserIdOrderByExpiryDateAsc(userId);
                break;
            case "name": // 식재료명 가나다순
                ingredients = ingredientRepository.findByUserIdOrderByNameAsc(userId);
                break;
            default:
                ingredients = ingredientRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        return ingredients.stream()
                .map(IngredientResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 카테고리별 식재료 목록 조회
    @Transactional(readOnly = true)
    public List<IngredientResponseDTO> getIngredientsByCategories(
            Long userId, String mainCategory, String subCategory, String detailCategory, String sortType) {

        // 사용자의 모든 식재료를 먼저 정렬 기준에 따라 가져옴
        List<Ingredient> allIngredients;

        // sortType에 따라 초기 정렬 적용
        switch (sortType) {
            case "latest": // 최신순 (기본값)
                allIngredients = ingredientRepository.findByUserIdOrderByCreatedAtDesc(userId);
                break;
            case "oldest": // 등록순
                allIngredients = ingredientRepository.findByUserIdOrderByCreatedAtAsc(userId);
                break;
            case "expiration": // 유통기한 임박순
                allIngredients = ingredientRepository.findByUserIdOrderByExpiryDateAsc(userId);
                break;
            case "name": // 식재료명 가나다순
                allIngredients = ingredientRepository.findByUserIdOrderByNameAsc(userId);
                break;
            default:
                allIngredients = ingredientRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        // 그 다음 카테고리별로 필터링
        List<Ingredient> filteredIngredients = allIngredients.stream()
                .filter(ingredient -> {
                    // 대분류 필터링
                    boolean mainCategoryMatch = mainCategory == null ||
                            mainCategory.isEmpty() ||
                            mainCategory.equals(ingredient.getMainCategory());

                    // 중분류 필터링
                    boolean subCategoryMatch = subCategory == null ||
                            subCategory.isEmpty() ||
                            subCategory.equals(ingredient.getSubCategory());

                    // 소분류 필터링
                    boolean detailCategoryMatch = detailCategory == null ||
                            detailCategory.isEmpty() ||
                            detailCategory.equals(ingredient.getDetailCategory());

                    return mainCategoryMatch && subCategoryMatch && detailCategoryMatch;
                })
                .collect(Collectors.toList());

        // 엔티티를 DTO로 변환하여 반환
        return filteredIngredients.stream()
                .map(IngredientResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 식재료 상세 정보 조회
    @Transactional(readOnly = true)
    public IngredientResponseDTO getIngredientDetail(Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("해당 식재료가 존재하지 않습니다."));

        return IngredientResponseDTO.fromEntity(ingredient);
    }
}