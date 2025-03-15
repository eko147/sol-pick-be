package kr.co.solpick.refrigerator.dto;

import kr.co.solpick.refrigerator.entity.Ingredient;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class IngredientResponseDTO {
    private Long id;
    private String name;
    private String emoji;
    private String image;
    private Integer quantity;
    private LocalDateTime expiryDate;
    private String mainCategory;
    private String subCategory;
    private String detailCategory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity를 DTO로 변환하는 정적 메서드
    public static IngredientResponseDTO fromEntity(Ingredient ingredient) {
        return IngredientResponseDTO.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .emoji(ingredient.getEmoji())
                .image(ingredient.getImage())
                .quantity(ingredient.getQuantity())
                .expiryDate(ingredient.getExpiryDate())
                .mainCategory(ingredient.getMainCategory())
                .subCategory(ingredient.getSubCategory())
                .detailCategory(ingredient.getDetailCategory())
                .createdAt(ingredient.getCreatedAt())
                .updatedAt(ingredient.getUpdatedAt())
                .build();
    }
}