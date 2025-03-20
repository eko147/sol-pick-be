package kr.co.solpick.point.controller;

import kr.co.solpick.point.dto.PointHistoryResponseDTO;
import kr.co.solpick.point.dto.PointSummaryResponseDTO;
import kr.co.solpick.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000/"})
public class PointController {

    private final PointService pointService;

    /**
     * 사용자의 포인트 요약 정보 조회
     * - 현재 포인트 잔액
     * - 포인트 적립/사용 요약
     */

    @GetMapping("/summary")
    public ResponseEntity<PointSummaryResponseDTO> getPointSummary() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        Map<String, Object> claims = (Map<String, Object>) principal;

        Object idObj = claims.get("id");
        Integer userId = (idObj instanceof Double) ?
                ((Double) idObj).intValue() :
                (Integer) idObj;





        log.info("포인트 요약 정보 요청: userId={}", userId);

        PointSummaryResponseDTO summary = pointService.getPointSummaryByUserId(userId);
        return ResponseEntity.ok(summary);
    }

    /**
     * 사용자의 포인트 적립/사용 내역 조회
     */

    @GetMapping("/history")
    public ResponseEntity<List<PointHistoryResponseDTO>> getPointHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        Map<String, Object> claims = (Map<String, Object>) principal;

        Object idObj = claims.get("id");
        Integer userId = (idObj instanceof Double) ?
                ((Double) idObj).intValue() :
                (Integer) idObj;


        log.info("포인트 이용내역 요청: userId={}", userId);

        List<PointHistoryResponseDTO> history = pointService.getPointHistoryByUserId(userId);

        if (history.isEmpty()) {
            log.info("포인트 이용내역 없음: userId={}", userId);
            return ResponseEntity.noContent().build();
        }

        log.info("포인트 이용내역 조회 성공: userId={}, 건수={}", userId, history.size());
        return ResponseEntity.ok(history);
    }

}