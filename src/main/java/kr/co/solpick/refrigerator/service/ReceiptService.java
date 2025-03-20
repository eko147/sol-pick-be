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

    // 상품명이 아닌 텍스트를 식별하는 헬퍼 메서드
    private boolean isLikelyNotProductName(String textContent) {
        // 1. 쉼표 없이 연속된 4자리 이상 숫자가 있는 경우 (연도, 코드 등)
        if (textContent.matches(".*\\d{4,}.*") && !textContent.matches(".*\\d{1,3},\\d{3}.*")) {
            return true;
        }

        // 2. 금액, 코드 등의 형태를 가진 패턴
        if (textContent.matches("^\\d{1,3}(,\\d{3})+$") || // 숫자와 쉼표만 있는 경우 (금액)
                textContent.matches("^[A-Z]\\d{3,}$") ||       // 알파벳 + 3자리 이상 숫자 (코드)
                textContent.matches("^\\d{2,}-\\d{2,}$")) {    // 숫자-숫자 형태 (코드)
            return true;
        }

//        // 3. 별표(*) 포함 항목 제외
//        if (textContent.matches(".*[*]+.*")) {
//            return true;
//        }

        // 4. 날짜 및 시간 패턴 제외
        if (textContent.matches(".*\\d{2,4}[./:\\-]\\d{1,2}[./:\\-]\\d{1,2}.*") ||  // 날짜 패턴 (2023.01.01, 23-01-01 등)
                textContent.matches(".*\\d{1,2}:\\d{2}(:\\d{2})?.*")) {             // 시간 패턴 (17:30, 17:30:25 등)
            return true;
        }

//        // 5. 한국인 이름 패턴 제외 (2-4자 한글 이름)
//        if (textContent.matches("^[가-힣]{2,4}$")) {
//            return true;
//        }

        return false;
    }

    // 영수증 OCR 결과에서 식재료명만 추출
    private List<String> extractIngredientNames(String ocrText) {
        List<String> ingredientNames = new ArrayList<>();

        // 제외할 키워드 목록
        List<String> excludeKeywords = Arrays.asList(
                "합계", "합 계", "과세", "부가세", "부기세", "총", "금액", "거래", "대상", "포인트", "잔액", "잔 액",
                "카드", "카 드", "상품권", "할인", "쿠폰", "쿠 폰", "일자", "결제", "기프티", "매출",
                "GS", "GS25", "서울", "아파트", "일부상품", "승인번호", "대한민국", "단지",
                "할부", "할 부", "제외", "교환", "환불", "영수증", "재미있는", "번호", "편의점",
                "인증기업", "판매", "점", "금액", "수량", "POS", "매장", "전화", "바코드",
                "명세서", "멤버십", "영업", "아이디", "지점", "배달", "가맹점", "법인", "신용",
                "현금", "현 금", "반품", "교환", "취소", "면세", "적립", "이용", "주문",
                "다이소", "국민가게", "소비자중심경영", "ISO", "체크카드", "승인금액", "결제취소", "회원가입", "혜택", "회원",
                "홈페이지", "관련", "문의", "중심", "경영", "품질", "시스템", "방침", "결제카드", "지참",
                "구입", "훼손", "불가", "취소", "소요", "네이버", "시간"
        );

        // 상품 라인 패턴들 - 여러 형태의 영수증 패턴을 정의
        // 1. "상품명 수량 가격" 패턴
        Pattern itemPattern1 = Pattern.compile("^([^0-9]+)\\s+(\\d+)\\s+([0-9,.]+)$");

        // 2. "상품명     가격" 패턴 (수량이 없는 경우)
        Pattern itemPattern2 = Pattern.compile("^([^0-9]+)\\s+([0-9,.]+)$");

        // 3. 상품명만 있고 수량과 가격이 다음 줄에 있는 경우를 위한 패턴
        Pattern quantityPricePattern = Pattern.compile("^\\s*(\\d+)\\s+([0-9,.]+)\\s*$");

        // 4. 다이소 영수증 패턴 (상품명 가격 수량 가격)
        Pattern daisoPattern = Pattern.compile("^(.+?)\\s+([0-9,.]+)\\s+(\\d+)\\s+([0-9,.]+)\\s*$");

        // 5. 상품명 다음 줄에 [상품코드] 패턴이 있는 경우
        Pattern productCodePattern = Pattern.compile("^\\s*\\[\\d+\\]\\s*$");
        Pattern productCodeParenPattern = Pattern.compile("^\\s*\\(\\d+\\)\\s*$");

        // 6. 순수 상품명 패턴 (특별한 형식 없이 상품명만 있는 경우)
        Pattern pureProductNamePattern = Pattern.compile("^[a-zA-Z가-힣0-9\\s]+$");

        // 줄 단위로 텍스트 분할
        String[] lines = ocrText.split("\n");

        // 각 라인 처리
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.isEmpty()) {
                continue;
            }

            log.debug("🔵 처리 중인 라인 {}: {}", i, line);

            // 제외 키워드 체크
            boolean shouldExclude = false;
            for (String keyword : excludeKeywords) {
                if (line.toLowerCase().contains(keyword.toLowerCase())) {
                    shouldExclude = true;
                    log.debug("🔵 제외 키워드 발견: {} in {}", keyword, line);
                    break;
                }
            }

            // 숫자나 날짜만 있는 줄, 제외 키워드가 있는 줄, 또는 너무 짧은 줄 건너뛰기
            if (shouldExclude ||
                    line.matches("^[0-9,]+$") ||
                    line.matches("^\\d{4}[/-]\\d{2}[/-]\\d{2}.*") ||
                    line.length() < 2 ||
                    line.matches("^[*]+$")) {  // 별표만 있는 줄도 제외
                log.debug("🔵 건너뛴 라인 (제외 조건): {}", line);
                continue;
            }

            // 패턴 1: "상품명 수량 가격" 형태 검사
            Matcher matcher1 = itemPattern1.matcher(line);
            if (matcher1.find()) {
                String itemName = matcher1.group(1).trim();
                // 상품명이 제외 키워드를 포함하는지 다시 체크
                boolean nameContainsExcludeKeyword = false;
                for (String keyword : excludeKeywords) {
                    if (itemName.toLowerCase().contains(keyword.toLowerCase())) {
                        nameContainsExcludeKeyword = true;
                        break;
                    }
                }

                if (!nameContainsExcludeKeyword && itemName.length() >= 2 && !isLikelyNotProductName(itemName)) {
                    log.debug("🔵 찾은 품목 (상품 수량 가격 패턴): {}", itemName);
                    ingredientNames.add(itemName);
                }
                continue;
            }

            // 패턴 2: "상품명 가격" 형태 검사 (수량 없음)
            Matcher matcher2 = itemPattern2.matcher(line);
            if (matcher2.find()) {
                String itemName = matcher2.group(1).trim();
                // 상품명이 제외 키워드를 포함하는지 다시 체크
                boolean nameContainsExcludeKeyword = false;
                for (String keyword : excludeKeywords) {
                    if (itemName.toLowerCase().contains(keyword.toLowerCase())) {
                        nameContainsExcludeKeyword = true;
                        break;
                    }
                }

                if (!nameContainsExcludeKeyword && itemName.length() >= 2 && !isLikelyNotProductName(itemName)) {
                    log.debug("🔵 찾은 품목 (상품 가격 패턴): {}", itemName);
                    ingredientNames.add(itemName);
                }
                continue;
            }

            // 패턴 3: 상품명만 있고 다음 줄에 수량과 가격이 있는 경우
            if (i + 1 < lines.length) {
                String nextLine = lines[i + 1].trim();
                Matcher quantityPriceMatcher = quantityPricePattern.matcher(nextLine);

                // 다음 줄이 수량과 가격 패턴이고, 현재 줄이 숫자로 시작하지 않는 경우
                if (!line.matches("^\\d+.*") && quantityPriceMatcher.matches() && !isLikelyNotProductName(line)) {
                    // 현재 줄에 제외 키워드가 포함되어 있지 않은지 확인
                    boolean nameContainsExcludeKeyword = false;
                    for (String keyword : excludeKeywords) {
                        if (line.toLowerCase().contains(keyword.toLowerCase())) {
                            nameContainsExcludeKeyword = true;
                            break;
                        }
                    }

                    if (!nameContainsExcludeKeyword && line.length() >= 2) {
                        log.debug("🔵 찾은 품목 (상품명 + 다음 줄 수량/가격): {}", line);
                        ingredientNames.add(line);
                        i++; // 다음 줄(수량/가격) 건너뛰기
                    }
                    continue;
                }
            }

            // 패턴 4: 다이소 영수증 패턴 (상품명 가격 수량 가격)
            Matcher daisoMatcher = daisoPattern.matcher(line);
            if (daisoMatcher.find()) {
                String itemName = daisoMatcher.group(1).trim();
                // 상품명이 제외 키워드를 포함하는지 체크
                boolean nameContainsExcludeKeyword = false;
                for (String keyword : excludeKeywords) {
                    if (itemName.toLowerCase().contains(keyword.toLowerCase())) {
                        nameContainsExcludeKeyword = true;
                        break;
                    }
                }

                if (!nameContainsExcludeKeyword && itemName.length() >= 2 && !isLikelyNotProductName(itemName)) {
                    log.debug("🔵 찾은 품목 (다이소 패턴): {}", itemName);
                    ingredientNames.add(itemName);
                }
                continue;
            }

            // 패턴 5: 상품명 다음 줄이 [상품코드] 또는 (상품코드) 패턴인 경우
            if (i + 1 < lines.length) {
                String nextLine = lines[i + 1].trim();
                if (productCodePattern.matcher(nextLine).matches() ||
                        productCodeParenPattern.matcher(nextLine).matches()) {
                    // 상품명이 제외 키워드를 포함하는지 체크
                    boolean nameContainsExcludeKeyword = false;
                    for (String keyword : excludeKeywords) {
                        if (line.toLowerCase().contains(keyword.toLowerCase())) {
                            nameContainsExcludeKeyword = true;
                            break;
                        }
                    }

                    if (!nameContainsExcludeKeyword && line.length() >= 2 && !isLikelyNotProductName(line)) {
                        log.debug("🔵 찾은 품목 (상품코드 패턴): {}", line);
                        ingredientNames.add(line);
                        i++; // 다음 줄(상품코드) 건너뛰기
                    }
                    continue;
                }
            }

            // 패턴 6: 순수 상품명 패턴 - 명확한 상품명처럼 보이는 경우 (주변 라인 컨텍스트 확인)
            if (pureProductNamePattern.matcher(line).matches() &&
                    !line.matches("^\\d+.*") && // 숫자로 시작하지 않고
                    line.length() >= 2 &&
                    !isLikelyNotProductName(line)) {

                // 이전 20줄과 다음 20줄 내에 가격 패턴이 있는지 확인
                boolean hasNearbyPrice = false;
                int startIdx = Math.max(0, i - 20);
                int endIdx = Math.min(lines.length - 1, i + 20);

                // 주변 라인에 상품명 관련 패턴이나 가격 패턴이 있는지 확인
                for (int j = startIdx; j <= endIdx; j++) {
                    if (j != i && // 현재 라인이 아니고
                            (lines[j].matches(".*\\d{1,3}(,\\d{3})+.*") || // 가격 패턴이 있거나
                                    productCodePattern.matcher(lines[j]).matches() || // 상품코드 패턴이거나
                                    productCodeParenPattern.matcher(lines[j]).matches())) { // 괄호 상품코드 패턴
                        hasNearbyPrice = true;
                        break;
                    }
                }

                // 주변에 관련 가격 정보가 있고, 명확한 상품명 형태이며, 제외 키워드가 없는 경우
                if (hasNearbyPrice && line.matches(".*[a-zA-Z가-힣]+.*")) {
                    // 제외 키워드 체크
                    boolean nameContainsExcludeKeyword = false;
                    for (String keyword : excludeKeywords) {
                        if (line.toLowerCase().contains(keyword.toLowerCase())) {
                            nameContainsExcludeKeyword = true;
                            break;
                        }
                    }

                    if (!nameContainsExcludeKeyword) {
                        log.debug("🔵 찾은 품목 (순수 상품명 패턴): {}", line);
                        ingredientNames.add(line);
                    }
                }
            }
        }

        // 최종 중복 제거 및 검증
        List<String> finalItems = new ArrayList<>();
        for (String item : ingredientNames) {
            // 중복 제거 및 추가 필터링
            if (!finalItems.contains(item) && !isLikelyNotProductName(item)) {
                finalItems.add(item);
            }
        }

        return finalItems;
    }
}