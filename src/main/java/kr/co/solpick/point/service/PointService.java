package kr.co.solpick.point.service;

import kr.co.solpick.card.repository.CardRepository;
import kr.co.solpick.member.entity.Member;
import kr.co.solpick.member.repository.MemberRepository;
import kr.co.solpick.point.dto.PointHistoryResponseDTO;
import kr.co.solpick.point.dto.PointSummaryResponseDTO;
import kr.co.solpick.point.entity.Point;
import kr.co.solpick.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;
import kr.co.solpick.card.entity.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;


    /**
     * 솔픽 회원 ID로 포인트 조회 <- 이건 추 후 수정
     */
    @Transactional(readOnly = true)
    public int getUserPoints(int userId) {
        log.info("솔픽 회원 ID로 포인트 조회: userId={}", userId);
        return pointRepository.findLatestPointBalanceByUserId(userId)
                .orElse(0);
    }

    /**
     * 레시픽 회원 ID로 포인트 조회
     */
    @Transactional(readOnly = true)
    public int getUserPointsByRecipickUserId(int recipickUserId) {
        log.info("레시픽 회원 ID로 포인트 조회: recipickUserId={}", recipickUserId);

        return pointRepository.findLatestPointBalanceByRecipickUserId(recipickUserId)
                .orElseGet(() -> {
                    log.warn("레시픽 회원 ID에 해당하는 포인트 정보가 없습니다: recipickUserId={}", recipickUserId);
                    return 0;
                });
    }

    /**
     * 포인트 사용 처리
     * @param recipickUserId 레시픽 사용자 ID
     * @param orderId 주문 ID
     * @param pointsToUse 사용할 포인트 양
     * @param totalPrice 주문 총액
     * @return 성공 여부
     */
    @Transactional
    public boolean usePoints(int recipickUserId, int orderId, int pointsToUse, int totalPrice) {
        log.info("포인트 사용 처리: recipickUserId={}, orderId={}, pointsToUse={}, totalPrice={}",
                recipickUserId, orderId, pointsToUse, totalPrice);

        // 레시픽 회원 ID로 솔픽 회원 조회
        Optional<Member> memberOpt = memberRepository.findByRecipickUserId(recipickUserId);
        if (memberOpt.isEmpty()) {
            log.warn("회원 정보를 찾을 수 없음: recipickUserId={}", recipickUserId);
            return false;
        }

        Member member = memberOpt.get();
        int userId = member.getId();

        // 사용자의 카드 조회 (사용자당 카드가 하나인 경우)
        Optional<Card> cardOpt = cardRepository.findByUserId(userId);
        if (cardOpt.isEmpty()) {
            log.warn("사용자의 카드 정보를 찾을 수 없음: userId={}", userId);
            return false;
        }

        Card card = cardOpt.get();
        Integer cardId = card.getId();

        // 현재 포인트 잔액 조회
        int currentPointBalance = pointRepository.findLatestPointBalanceByUserId(userId)
                .orElse(0);

        // 포인트 부족 체크
        if (currentPointBalance < pointsToUse) {
            log.warn("포인트 부족: currentBalance={}, requested={}", currentPointBalance, pointsToUse);
            return false;
        }

        // 포인트 사용 내역 저장
        Point pointUsage = Point.builder()
                .userId(userId)
                .cardId(cardId)  // 카드 ID 설정
                .orderId(orderId)
                .pointAmount(-pointsToUse)
                .pointBalance(currentPointBalance - pointsToUse)
                .pointType("USE")
                .transactionAmount(totalPrice)
                .description("레시픽 주문")
//                .createdDate(LocalDate.now())
                .build();

        pointRepository.save(pointUsage);
        log.info("포인트 사용 처리 완료: userId={}, pointsUsed={}, newBalance={}",
                userId, pointsToUse, pointUsage.getPointBalance());

        // 구매 금액의 2% 적립 (솔픽 카드 혜택)
        int earnedPoints = (int)(totalPrice * 0.02);
        if (earnedPoints > 0) {
            Point pointEarning = Point.builder()
                    .userId(userId)
                    .cardId(cardId)  // 카드 ID 설정
                    .orderId(orderId)
                    .pointAmount(earnedPoints)
                    .pointBalance(pointUsage.getPointBalance() + earnedPoints)
                    .pointType("EARN")
                    .transactionAmount(totalPrice)
                    .description("레시픽 주문")
//                    .createdDate(LocalDate.now())
                    .build();

            pointRepository.save(pointEarning);
            log.info("포인트 적립 처리 완료: userId={}, earnedPoints={}, newBalance={}",
                    userId, earnedPoints, pointEarning.getPointBalance());
        }

        return true;
    }

    /**
     * 사용자 ID로 포인트 요약 정보 조회
     */
    @Transactional(readOnly = true)
    public PointSummaryResponseDTO getPointSummaryByUserId(Integer userId) {
        log.info("포인트 요약 정보 조회: userId={}", userId);

        // 1. 현재 포인트 잔액 조회
        Integer currentPoints = pointRepository.findLatestPointBalanceByUserId(userId).orElse(0);

        // 2. 포인트 적립/사용 합계 조회
        Integer totalEarned = pointRepository.findTotalEarnedPointsByUserId(userId);
        Integer totalUsed = pointRepository.findTotalUsedPointsByUserId(userId);

        // 3. 메시지 생성 (예시)
        String message = "총 " + currentPoints + "포인트 있어요!";

        return PointSummaryResponseDTO.builder()
                .currentPoints(currentPoints)
                .totalEarnedPoints(totalEarned)
                .totalUsedPoints(totalUsed)
                .message(message)
                .build();
    }

    /**
     * 사용자 ID로 포인트 이용 내역 조회
     */
    @Transactional(readOnly = true)
    public List<PointHistoryResponseDTO> getPointHistoryByUserId(Integer userId) {
        log.info("포인트 이용 내역 조회: userId={}", userId);

        // 1. 포인트 내역 조회
        List<Point> pointHistory = pointRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        // 2. DTO로 변환
        return convertToDTO(pointHistory);
    }

    /**
     * Point 엔티티 목록을 PointHistoryResponseDTO 목록으로 변환
     */
    private List<PointHistoryResponseDTO> convertToDTO(List<Point> points) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M.dd");

        return points.stream()
                .map(point -> {
                    String formattedDate = point.getCreatedAt().toLocalDate().format(formatter);

                    // 내역 설명 처리
                    String description = point.getDescription();

                    // 유형 및 금액 처리
                    String type = point.getPointType();
                    Integer amount = Math.abs(point.getPointAmount());

                    return PointHistoryResponseDTO.builder()
                            .pointId(point.getId())
                            .date(formattedDate)
                            .description(description)
                            .amount(amount)
                            .type(type)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 레시피 완성 포인트 적립 메서드
     * @param userId 쏠픽 사용자 ID
     * @param cardId 신용카드 ID
     * @param pointAmount 포인트액
     */
    @Transactional
    public void addRecipeCompletionPoints(Integer userId, Integer cardId, Integer pointAmount) {
        log.info("푸디캣 포인트 적립: userId={}, cardId={}, amount={}", userId, cardId, pointAmount);

        // 현재 포인트 잔액 조회
        Integer currentPointBalance = pointRepository.findLatestPointBalanceByUserId(userId)
                .orElse(0);

        // 카드 ID는 필수 값으로 설정

        // 포인트 적립 내역 저장
        Point pointEarning = Point.builder()
                .userId(userId)
                .cardId(cardId) // 카드 ID 필수
                .pointAmount(pointAmount)
                .pointBalance(currentPointBalance + pointAmount)
                .pointType("EARN")
                .description("푸디캣 포인트") // 고정 설명으로 변경
                .transactionAmount(0)
                .build();

        pointRepository.save(pointEarning);

        log.info("푸디캣 포인트 적립 완료: userId={}, cardId={}, amount={}, newBalance={}",
                userId, cardId, pointAmount, pointEarning.getPointBalance());
    }
}
