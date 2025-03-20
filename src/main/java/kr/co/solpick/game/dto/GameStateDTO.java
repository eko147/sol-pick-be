package kr.co.solpick.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameStateDTO {
    private Integer level;
    private Integer currentExp;
    private Integer energy;
    private Integer food;
    private Integer ingredientsCount;
}