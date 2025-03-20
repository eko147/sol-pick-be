package kr.co.solpick.api.external.point.controller;

import kr.co.solpick.api.external.point.dto.PointRequestDTO;
import kr.co.solpick.api.external.point.dto.PointResponseDTO;
import kr.co.solpick.api.external.ApiKeyService;
import kr.co.solpick.api.external.point.dto.PointUpdateRequestDTO;
import kr.co.solpick.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/solpick/api")
@RequiredArgsConstructor
public class ExternalPointController {

    private final PointService pointService;
    private final ApiKeyService apiKeyService;

    @PostMapping("/points")  // 여기서 /points를 추가
    public PointResponseDTO getUserPoints(@RequestBody PointRequestDTO request) {
        log.info("포인트 조회 API 요청 받음: memberId={}", request.getMemberId());

        // API 키 유효성 검사
        if (!apiKeyService.validateApiKey(request.getApiKey())) {
            log.warn("잘못된 API 키: {}", request.getApiKey());
            return PointResponseDTO.builder()
                    .success(false)
                    .message("유효하지 않은 API 키입니다.")
                    .points(0)
                    .build();
        }

        try {
            // 레시픽 회원 ID로 포인트 조회
            int points = pointService.getUserPointsByRecipickUserId(request.getMemberId());
            log.info("포인트 조회 성공: recipickUserId={}, points={}", request.getMemberId(), points);

            return PointResponseDTO.builder()
                    .success(true)
                    .message("포인트 조회 성공")
                    .points(points)
                    .build();
        } catch (Exception e) {
            log.error("포인트 조회 중 오류 발생", e);
            return PointResponseDTO.builder()
                    .success(false)
                    .message("포인트 조회 중 오류가 발생했습니다: " + e.getMessage())
                    .points(0)
                    .build();
        }
    }

    @PostMapping("/points/update")
    public PointResponseDTO updatePoints(@RequestBody PointUpdateRequestDTO request) {
        log.info("포인트 업데이트 API 요청 받음: memberId={}, orderId={}, pointsUsed={}, totalPrice={}",
                request.getMemberId(), request.getOrderId(), request.getPointsUsed(), request.getTotalPrice());

        // API 키 유효성 검사
        if (!apiKeyService.validateApiKey(request.getApiKey())) {
            log.warn("잘못된 API 키: {}", request.getApiKey());
            return PointResponseDTO.builder()
                    .success(false)
                    .message("유효하지 않은 API 키입니다.")
                    .points(0)
                    .build();
        }

        try {
            // 포인트 사용 처리
            boolean success = pointService.usePoints(
                    request.getMemberId(),
                    request.getOrderId(),
                    request.getPointsUsed(),
                    request.getTotalPrice()
            );

            if (success) {
                // 현재 잔여 포인트 조회
                int remainingPoints = pointService.getUserPointsByRecipickUserId(request.getMemberId());

                return PointResponseDTO.builder()
                        .success(true)
                        .message("포인트 사용 처리가 완료되었습니다.")
                        .points(remainingPoints)
                        .build();
            } else {
                return PointResponseDTO.builder()
                        .success(false)
                        .message("포인트 사용 처리에 실패했습니다.")
                        .points(0)
                        .build();
            }
        } catch (Exception e) {
            log.error("포인트 업데이트 중 오류 발생", e);
            return PointResponseDTO.builder()
                    .success(false)
                    .message("포인트 업데이트 중 오류가 발생했습니다: " + e.getMessage())
                    .points(0)
                    .build();
        }
    }
}