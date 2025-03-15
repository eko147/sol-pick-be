package kr.co.solpick.api.external.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointResponseDTO {
    private int points;
    private boolean success;
    private String message;
}