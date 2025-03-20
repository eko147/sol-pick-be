package kr.co.solpick.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryResultDTO {
    private Boolean isNewlyDiscovered;
    private Integer newCount;
    private Boolean isRecipeCompleted;
}