package kr.co.solpick.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletedRecipeDTO {
    private Integer id;
    private Integer userId;
    private Integer recipeId;
    private Integer pointAmount;
    private LocalDateTime completionDate;
}