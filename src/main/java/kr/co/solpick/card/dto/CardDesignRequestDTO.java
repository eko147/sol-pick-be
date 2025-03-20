package kr.co.solpick.card.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDesignRequestDTO {
    private Integer designId;
    private Integer userId;
    private Integer backgroundId;
    private String stickersData;
}
