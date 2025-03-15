package kr.co.solpick.api.external.point.dto;

import lombok.Data;

@Data
public class PointRequestDTO {
    private int memberId;
    private String apiKey;
}