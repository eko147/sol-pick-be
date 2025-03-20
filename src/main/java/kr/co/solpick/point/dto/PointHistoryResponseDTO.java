package kr.co.solpick.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryResponseDTO {
    private Integer pointId;
    private String date;  // 날짜 (2.25 형식으로 표시)
    private String description;  // 내역 설명 (레시픽 쇼핑, 고양이게임 적립 등)
    private Integer amount;  // 금액 (2,614원 형식)
    private String type;  // 유형 (EARN, USE)
}