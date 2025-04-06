package kr.co.solpick.member.controller;

import java.util.List;
import java.util.Map;

import kr.co.solpick.member.entity.Member;
import kr.co.solpick.member.repository.MemberRepository;
import kr.co.solpick.member.service.MemberService;
import kr.co.solpick.order.dto.OrderHistoryResponseDTO;
import kr.co.solpick.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
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
    private final MemberRepository memberRepository;
    private final MemberService memberService;  // MemberService 주입 추가

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

    // 마지막 동기화 주문 ID 가져오기
    @GetMapping("/last-sync-order-id")
    public ResponseEntity<Integer> getLastSyncOrderId() {
        // 사용자 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Map<String, Object> claims = (Map<String, Object>) principal;

        Integer memberId = claims.get("id") instanceof Double ?
                ((Double) claims.get("id")).intValue() :
                (Integer) claims.get("id");

        log.info("마지막 동기화 주문 ID 조회 요청: 사용자 ID={}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        Integer lastSyncOrderId = member.getLastSyncOrderId();
        log.info("마지막 동기화 주문 ID 조회 결과: 사용자 ID={}, 마지막 동기화 주문 ID={}", memberId, lastSyncOrderId);

        return ResponseEntity.ok(lastSyncOrderId);
    }

    // 마지막 동기화 주문 ID 업데이트 (새로 추가된 엔드포인트)
    @PostMapping("/update-last-sync-order-id")
    public ResponseEntity<Boolean> updateLastSyncOrderId(@RequestBody Map<String, Integer> request) {
        // 사용자 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Map<String, Object> claims = (Map<String, Object>) principal;

        Integer memberId = claims.get("id") instanceof Double ?
                ((Double) claims.get("id")).intValue() :
                (Integer) claims.get("id");

        Integer orderId = request.get("orderId");
        log.info("마지막 동기화 주문 ID 업데이트 요청: 사용자 ID={}, 주문 ID={}", memberId, orderId);

        if (orderId == null) {
            log.error("주문 ID가 null입니다.");
            return ResponseEntity.badRequest().body(false);
        }

        // 멤버 서비스의 메서드 사용
        int updatedRows = memberService.updateLastSyncOrderId(memberId, orderId);
        boolean success = updatedRows > 0;

        if (success) {
            log.info("마지막 동기화 주문 ID 업데이트 성공: 사용자 ID={}, 주문 ID={}", memberId, orderId);
        } else {
            // 업데이트 실패 시 직접 저장 시도
            log.warn("SQL 업데이트 실패, JPA 방식으로 재시도");
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

            member.setLastSyncOrderId(orderId);
            Member updatedMember = memberRepository.save(member);

            success = updatedMember.getLastSyncOrderId().equals(orderId);
            log.info("JPA 방식 업데이트 결과: {}-> {}, 성공={}",
                    orderId, updatedMember.getLastSyncOrderId(), success);
        }

        return ResponseEntity.ok(success);
    }
}