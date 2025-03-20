package kr.co.solpick.external.recipick.dto;

import lombok.Data;

@Data
public class RecipickStepResponseDTO {
    private int stepId;
    private int sort;
    private String description;
    private String imgUrl;
    private int time;
}