package kr.co.solpick.card.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardStickersRequestDTO {
    private Integer designId;
    private String stickersData; // JSON 형태의 스티커 배치 정보
}
