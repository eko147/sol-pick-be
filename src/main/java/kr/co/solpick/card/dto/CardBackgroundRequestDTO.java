package kr.co.solpick.card.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardBackgroundRequestDTO {
    private Integer userId;
    private Integer backgroundId;
}
