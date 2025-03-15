package kr.co.solpick.refrigerator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptOcrRequestDTO {
    private Long userId;
    private String receiptImage; // Base64 인코딩된 이미지
}