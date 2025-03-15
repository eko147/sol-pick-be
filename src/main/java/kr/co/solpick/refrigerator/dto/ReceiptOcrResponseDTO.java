package kr.co.solpick.refrigerator.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReceiptOcrResponseDTO {
    private String ocrText;       // 전체 OCR 텍스트 (디버깅/참고용)
    private List<String> ingredientNames;  // 추출된 식재료명 목록
}