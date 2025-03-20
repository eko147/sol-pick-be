package kr.co.solpick.card.controller;

import kr.co.solpick.card.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // 이 부분 추가
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j  // 이 부분 추가
@RestController
@RequestMapping("/solpick/api/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/has-card")
    public ResponseEntity<Map<String, Boolean>> hasCard() {
        // SecurityContextHolder에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Map<String, Object> claims = (Map<String, Object>) principal;

        // user_id 가져오기 (Integer 타입 변환 처리)
        Object idObj = claims.get("id");
        Integer userId = (idObj instanceof Double) ?
                ((Double) idObj).intValue() :
                (Integer) idObj;

        // 디버깅 로그 추가
        log.info("카드 소유 여부 확인: userId={}", userId);
        boolean hasCard = cardService.hasCard(userId);
        log.info("카드 소유 결과: userId={}, hasCard={}", userId, hasCard);

        return ResponseEntity.ok(Map.of("hasCard", hasCard));
    }
}