package kr.co.solpick.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointSummaryResponseDTO {
    private Integer currentPoints;      // 현재 사용 가능한 포인트
    private Integer totalEarnedPoints;  // 총 적립된 포인트
    private Integer totalUsedPoints;    // 총 사용한 포인트
    private String message;             // "저번주보다 3,700원 더 모았어요!" 같은 메시지
}