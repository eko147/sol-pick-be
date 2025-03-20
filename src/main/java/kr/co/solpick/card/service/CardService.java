package kr.co.solpick.card.service;

import kr.co.solpick.card.entity.Card;
import kr.co.solpick.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // 이 부분 추가
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j  // 이 부분 추가
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    /**
     * 사용자가 카드를 가지고 있는지 확인
     * @param userId 사용자 ID
     * @return 카드 소유 여부
     */
    public boolean hasCard(Integer userId) {
        if (userId == null) {
            log.warn("사용자 ID가 null입니다.");
            return false;
        }

        boolean result = cardRepository.existsByUserId(userId);
        log.info("카드 조회 결과: userId={}, exists={}", userId, result);
        return result;
//        return cardRepository.existsByUserId(userId);
    }

    /**
     * 카드 발급 시 날짜 설정
     * @param card 카드 엔티티
     */
    public void setCardDates(Card card) {
        // 발급일은 현재 날짜로 설정
        LocalDate today = LocalDate.now();
        card.setIssueDate(today);

        // 만료일은 3년 후로 설정 (예시)
        LocalDate expiryDate = today.plusYears(3);
        card.setExpiredAt(expiryDate);
    }

    /**
     * 카드가 만료되었는지 확인
     * @param cardId 카드 ID
     * @return 만료 여부
     */
    public boolean isCardExpired(Integer cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("카드를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();
        return today.isAfter(card.getExpiredAt());
    }
}