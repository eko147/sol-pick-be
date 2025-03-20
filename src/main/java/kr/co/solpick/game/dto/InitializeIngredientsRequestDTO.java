package kr.co.solpick.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitializeIngredientsRequestDTO {
    private Integer userId;
    private Integer recipeId;
    private List<IngredientInfoDTO> ingredients;
}