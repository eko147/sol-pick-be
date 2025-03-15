package kr.co.solpick.refrigerator.controller;

import kr.co.solpick.refrigerator.dto.ReceiptOcrRequestDTO;
import kr.co.solpick.refrigerator.dto.ReceiptOcrResponseDTO;
import kr.co.solpick.refrigerator.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solpick/refrigerator/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    // 영수증 이미지 처리 및 식재료명 추출
    @PostMapping("/ocr")
    public ResponseEntity<ReceiptOcrResponseDTO> processReceipt(@RequestBody ReceiptOcrRequestDTO requestDto) {
        ReceiptOcrResponseDTO responseDto = receiptService.processReceiptOcr(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}