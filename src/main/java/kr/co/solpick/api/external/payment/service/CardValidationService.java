package kr.co.solpick.api.external.payment.service;

import kr.co.solpick.api.external.payment.dto.VerifyCardResponseDTO;
import kr.co.solpick.member.entity.Member;
import kr.co.solpick.member.repository.MemberRepository;
import kr.co.solpick.point.entity.Point;
import kr.co.solpick.point.repository.PointRepository;
import kr.co.solpick.card.repository.CardRepository;
import kr.co.solpick.card.entity.Card;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardValidationService {

    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    /**
     * 카드 검증 및 포인트 사용 처리
     */
    @Transactional
    public VerifyCardResponseDTO verifyCardAndUsePoints(
            int recipickUserId,
            String cardNumber,
            String cardExpiry) {

        log.info("카드 검증 시작: recipickUserId={}", recipickUserId);

        // 1. 먼저 회원 정보 조회
        Optional<Member> memberOpt = memberRepository.findByRecipickUserId(recipickUserId);
        if (memberOpt.isEmpty()) {
            return VerifyCardResponseDTO.builder()
                    .success(false)
                    .message("회원 정보를 찾을 수 없습니다.")
                    .isValid(false)
                    .build();
        }

        Member member = memberOpt.get();

        // 2. 사용자의 카드 정보 조회 및 검증
        Optional<Card> cardOpt = cardRepository.findByUserIdAndCardNumberAndCardStatus(
                member.getId(), cardNumber, "ACTIVE");

        if (cardOpt.isEmpty()) {
            return VerifyCardResponseDTO.builder()
                    .success(false)
                    .message("등록된 카드 정보를 찾을 수 없습니다.")
                    .isValid(false)
                    .build();
        }

        Card card = cardOpt.get();

        // 카드 유효기간 검증
        if (!isValidCardExpiry(card.getExpiredAt(), cardExpiry)) {
            return VerifyCardResponseDTO.builder()
                    .success(false)
                    .message("카드 유효기간이 올바르지 않습니다.")
                    .isValid(false)
                    .build();
        }

        // 6. 응답 생성
        return VerifyCardResponseDTO.builder()
                .success(true)
                .message("카드 검증이 완료되었습니다.")
                .isValid(true)
                .build();
    }

    /**
     * 카드 유효기간 검증
     */
    /**
     * 카드 유효기간 검증
     */
    private boolean isValidCardExpiry(LocalDate storedExpiryDate, String inputExpiry) {
        try {
            // 입력 형식 검증 (MM/YY)
            if (!inputExpiry.matches("\\d{2}/\\d{2}")) {
                return false;
            }

            // 입력된 만료일을 파싱
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            LocalDate parsedDate = LocalDate.parse("01/" + inputExpiry, DateTimeFormatter.ofPattern("dd/MM/yy"))
                    .withDayOfMonth(1).plusMonths(1).minusDays(1); // 해당 월의 마지막 날

            // 년도와 월이 일치하는지 비교
            return storedExpiryDate.getYear() == parsedDate.getYear() &&
                    storedExpiryDate.getMonth() == parsedDate.getMonth();
        } catch (Exception e) {
            log.error("유효기간 검증 중 오류 발생", e);
            return false;
        }
    }
}