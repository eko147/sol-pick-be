package kr.co.solpick.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveredIngredientsDTO {
    private String ingredientName;
    private Boolean discovered;
    private Integer count;
    private Integer requiredQuantity;
}