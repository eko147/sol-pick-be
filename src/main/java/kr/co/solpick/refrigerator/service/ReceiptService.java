package kr.co.solpick.refrigerator.service;

import kr.co.solpick.refrigerator.dto.ReceiptOcrRequestDTO;
import kr.co.solpick.refrigerator.dto.ReceiptOcrResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptService {

    private final OcrService ocrService;

    // 영수증 이미지 처리 및 식재료명만 추출
    public ReceiptOcrResponseDTO processReceiptOcr(ReceiptOcrRequestDTO requestDto) {
        try {
            // Base64 이미지 처리
            String base64Image = requestDto.getReceiptImage();
            log.debug("🟢 수신된 이미지 데이터 길이: {}", base64Image != null ? base64Image.length() : 0);

            // data:image/jpeg;base64, 와 같은 접두사 처리
            String[] parts = base64Image.split(",");
            String base64 = parts.length > 1 ? parts[1] : base64Image;

            // 디코딩
            byte[] imageBytes = Base64.getDecoder().decode(base64);
            log.debug("🟢 디코딩된 이미지 바이트 길이: {}", imageBytes.length);

            // OCR 처리
            String ocrResult = ocrService.processImage(imageBytes);
            log.debug("🟢 OCR 결과: {}", ocrResult);

            // OCR 결과에서 식재료명만 추출
            List<String> ingredientNames = extractIngredientNames(ocrResult);
            log.debug("🟢 추출된 식재료 목록: {}", ingredientNames);

            return ReceiptOcrResponseDTO.builder()
                    .ocrText(ocrResult)
                    .ingredientNames(ingredientNames)
                    .build();
        } catch (IllegalArgumentException e) {
            // Base64 디코딩 오류
            log.error("🔴 Base64 디코딩 오류: ", e);
            throw new RuntimeException("이미지 형식이 올바르지 않습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 오류
            log.error("🔴 OCR 처리 중 오류 발생: ", e);
            throw new RuntimeException("OCR 처리 실패: " + e.getMessage(), e);
        }
    }

    // 영수증 OCR 결과에서 식재료명만 추출
    private List<String> extractIngredientNames(String ocrText) {
        List<String> ingredientNames = new ArrayList<>();

        // 줄 단위로 텍스트 분할
        String[] lines = ocrText.split("\n");

        // 제외할 키워드 목록
        List<String> excludeKeywords = Arrays.asList(
                "합계", "합 계", "과세", "부가세", "총", "금액", "거래", "대상", "포인트", "잔액", "잔 액",
                "카드", "카 드", "상품권", "할인", "쿠폰", "쿠 폰", "일자", "결제", "기프티", "매출"
        );

        // 각 라인 처리
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            log.debug("🔵 처리 중인 라인 {}: {}", i, line);

            // 제외 키워드 체크
            boolean shouldExclude = false;
            for (String keyword : excludeKeywords) {
                if (line.contains(keyword)) {
                    shouldExclude = true;
                    break;
                }
            }

            // 숫자나 날짜만 있는 줄, 제외 키워드가 있는 줄, 또는 너무 짧은 줄 건너뛰기
            if (shouldExclude || line.matches("^[0-9,]+$") ||
                    line.matches("^\\d{4}[/-]\\d{2}[/-]\\d{2}.*") ||
                    line.length() < 2) {
                log.debug("🔵 건너뛴 라인 (비품목): {}", line);
                continue;
            }

            // 다음 줄이 숫자만 있는지 확인 (수량 패턴)
            boolean hasQuantityPattern = (i + 1 < lines.length &&
                    lines[i + 1].trim().matches("^[0-9]+$"));

            // 그 다음 줄이 가격 패턴인지 확인
            boolean hasPricePattern = (i + 2 < lines.length &&
                    lines[i + 2].trim().matches("^[0-9,]+$"));

            // 품목명 + 수량 + 가격 패턴인 경우
            if (hasQuantityPattern && hasPricePattern) {
                String itemName = line;
                log.debug("🔵 찾은 품목 (수량+가격 패턴): {}", itemName);
                ingredientNames.add(itemName);
                continue;
            }

            // 직접적인 품목명인지 추가 검사
            // 알파벳, 한글, 숫자, 띄어쓰기만 포함되고 알파벳이나 한글이 최소 1자 이상
            if (line.matches("^[a-zA-Z가-힣0-9\\s]+$") &&
                    line.matches(".*[a-zA-Z가-힣]+.*") &&
                    !line.matches("^[0-9]+$")) {

                // 너무 짧은 단어나 수량을 나타내는 숫자 단독인 경우 제외
                if (line.length() >= 2 && !line.matches("^[0-9]+$")) {
                    log.debug("🔵 찾은 품목 (직접 품목명): {}", line);
                    ingredientNames.add(line);
                }
            }
        }

        return ingredientNames;
    }
}