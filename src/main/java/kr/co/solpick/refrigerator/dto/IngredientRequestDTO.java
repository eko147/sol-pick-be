package kr.co.solpick.refrigerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class IngredientRequestDTO {
    private Long userId;
    private String name;
    private String emoji;
    private String image;
    private Integer quantity;
    private LocalDateTime expiryDate;
    private String mainCategory;
    private String subCategory;
    private String detailCategory;
}