package kr.co.solpick.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoverIngredientRequestDTO {
    private Integer userId;
    private Integer recipeId;
    private String ingredientName;
    private Integer recipePoints;
}