package kr.co.solpick.refrigerator.controller;

import kr.co.solpick.external.recipick.client.RecipickOrderApiClient;
import kr.co.solpick.member.entity.Member;
import kr.co.solpick.member.repository.MemberRepository;
import kr.co.solpick.member.service.MemberService;
import kr.co.solpick.refrigerator.dto.IngredientRequestDTO;
import kr.co.solpick.refrigerator.dto.IngredientResponseDTO;
import kr.co.solpick.refrigerator.dto.RecipickOrderItemDTO;
import kr.co.solpick.refrigerator.service.IngredientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/solpick/refrigerator")
@RequiredArgsConstructor
@Slf4j
public class RecipickController {

    private final RecipickOrderApiClient recipickOrderApiClient;
    private final MemberRepository memberRepository;
    private final IngredientService ingredientService;
    private final MemberService memberService; // MemberService 주입 추가

    /* 레시픽 구매 재료 목록 조회 - 마지막 동기화된 주문 ID 이후 주문만 필터링 */
    @GetMapping("/ingredients/sync")
    public ResponseEntity<List<RecipickOrderItemDTO>> getIngredients(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "0") Integer lastOrderId) {

        log.info("레시픽 구매 재료 목록 조회 요청: userId={}, lastOrderId={}", userId, lastOrderId);

        // 레시픽 API 호출
        List<RecipickOrderItemDTO> allItems = recipickOrderApiClient.getIngredients(userId);

        // 마지막 동기화 주문 ID 이후의 새 주문만 필터링
        List<RecipickOrderItemDTO> newItems = allItems.stream()
                .filter(item -> item.getOhId() > lastOrderId)
                .collect(Collectors.toList());

        log.info("필터링된 구매 재료 목록: {} 건 중 {} 건", allItems.size(), newItems.size());

        if (newItems.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(newItems);
    }

    /* 레시픽 구매 재료를 솔픽 냉장고에 동기화 */
    @PostMapping("/ingredients/register")
    @Transactional
    public ResponseEntity<Map<String, Object>> registerIngredients(
            @RequestParam Integer recipickUserId,
            @RequestBody List<RecipickOrderItemDTO> items) {

        log.info("레시픽 구매 재료 솔픽 냉장고 등록 요청: recipickUserId={}, 항목 수={}", recipickUserId, items.size());

        // 레시픽 사용자 ID로 솔픽 사용자 ID 조회
        Member member = memberRepository.findByRecipickUserId(recipickUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 레시픽 사용자와 연결된 솔픽 사용자가 없습니다."));

        Integer solpickUserId = member.getId();
        log.info("레시픽 ID {} -> 솔픽 ID {}", recipickUserId, solpickUserId);

        // 등록된 식재료 ID 리스트와 최대 주문 ID 추적
        Map<String, Object> response = new HashMap<>();
        List<Long> registeredIds = items.stream()
                .map(item -> registerIngredient(item, solpickUserId))
                .filter(id -> id != null)
                .collect(Collectors.toList());

        // 등록된 항목이 있는 경우 최대 주문 ID 계산
        int maxOrderId = items.stream()
                .mapToInt(RecipickOrderItemDTO::getOhId)
                .max()
                .orElse(0);

        // 회원의 마지막 동기화 주문 ID 업데이트 - MemberService 사용
        log.info("회원 ID {}의 마지막 동기화 주문 ID 업데이트 시도: {} -> {}",
                solpickUserId, member.getLastSyncOrderId(), maxOrderId);

        try {
            // MemberService의 메서드 사용
            int updatedRows = memberService.updateLastSyncOrderId(solpickUserId, maxOrderId);
            log.info("회원 ID {}의 마지막 동기화 주문 ID 업데이트 결과: {} 행 영향받음", solpickUserId, updatedRows);

            if (updatedRows == 0) {
                // 업데이트 실패 시 직접 저장 시도
                log.warn("SQL 업데이트 실패, JPA 방식으로 재시도");
                member.setLastSyncOrderId(maxOrderId);
                Member updatedMember = memberRepository.save(member);
                log.info("JPA 방식 업데이트 결과: {} -> {}",
                        maxOrderId, updatedMember.getLastSyncOrderId());
            }
        } catch (Exception e) {
            log.error("회원 ID {}의 마지막 동기화 주문 ID 업데이트 실패: {}", solpickUserId, e.getMessage(), e);
        }

        response.put("success", true);
        response.put("count", registeredIds.size());
        response.put("registeredIds", registeredIds);
        response.put("latestOrderId", maxOrderId);
        response.put("message", registeredIds.size() + "개의 레시픽 구매 상품이 냉장고에 추가되었습니다.");

        return ResponseEntity.ok(response);
    }

    /* 개별 식재료 등록 처리*/
    private Long registerIngredient(RecipickOrderItemDTO item, Integer solpickUserId) {
        try {
            // 항상 현재 한국 시간(KST) 기준으로 설정
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
            LocalDateTime expiryDate = now.plusDays(7);

            log.info("식재료 {} 날짜 정보: 현재={}, 유통기한={}",
                    item.getName(), now, expiryDate);

            // 식재료 등록 요청 DTO 준비 - 솔픽 사용자 ID 사용
            IngredientRequestDTO requestDTO = new IngredientRequestDTO();
            requestDTO.setUserId(solpickUserId.longValue());  // 솔픽 사용자 ID 사용
            requestDTO.setName(item.getName());
            requestDTO.setEmoji(item.getEmoji());
            requestDTO.setImage(item.getImage());
            requestDTO.setQuantity(item.getQuantity());
            requestDTO.setExpiryDate(expiryDate); // 현재로부터 7일 후로 설정
            requestDTO.setMainCategory(item.getMainCategory());
            requestDTO.setSubCategory(item.getSubCategory());
            requestDTO.setDetailCategory(item.getDetailCategory());
            requestDTO.setCreatedAt(now); // 현재 시간을 등록일로 설정

            // 식재료 등록
            IngredientResponseDTO response = ingredientService.addIngredient(requestDTO);
            log.info("식재료 등록 성공: {}, ID={}, 사용자 ID={}", item.getName(), response.getId(), solpickUserId);

            return response.getId();
        } catch (Exception e) {
            log.error("식재료 등록 실패: {}, 오류={}", item.getName(), e.getMessage(), e);
            return null;
        }
    }
}