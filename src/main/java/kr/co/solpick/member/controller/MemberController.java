package kr.co.solpick.member.controller;

import java.util.List;
import java.util.Map;

import kr.co.solpick.order.dto.OrderHistoryResponseDTO;
import kr.co.solpick.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000/"})
public class MemberController {

    private final OrderService orderService;

    @GetMapping("/order")
    public ResponseEntity<List<OrderHistoryResponseDTO>> getOrderHistory() {
        // SecurityContextHolder에서 인증 정보 가져오기
        //요청이 서버에 도달하면, 먼저 TokenCheckFilter가 실행 이 필터는 요청 헤더에서 JWT 토큰을 추출하고 검증
        //토큰 검증이 성공하면, TokenCheckFilter는 토큰에서 추출한 정보(claims)를 SecurityContextHolder에 저장
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        Map<String, Object> claims = (Map<String, Object>) principal;


        Object idObj = claims.get("recipickUserId");
        Integer memberId = (idObj instanceof Double) ?
                ((Double) idObj).intValue() :
                (Integer) idObj;

        log.info("주문 내역 요청 수신: recipickmemberId={}", memberId);

        List<OrderHistoryResponseDTO> orderHistory = orderService.getOrderHistory(memberId);

        if (orderHistory.isEmpty()) {
            log.info("주문 내역 없음: recipickmemberId={}", memberId);
            return ResponseEntity.noContent().build();
        }

        log.info("주문 내역 조회 성공: memberId={}, 건수={}", memberId, orderHistory.size());
        return ResponseEntity.ok(orderHistory);
    }
}