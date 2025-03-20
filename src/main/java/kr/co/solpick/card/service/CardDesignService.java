package kr.co.solpick.card.service;

import jakarta.transaction.Transactional;
import kr.co.solpick.card.dto.CardCompleteResponseDTO;
import kr.co.solpick.card.dto.CardDesignRequestDTO;
import kr.co.solpick.card.dto.CardIssueRequestDTO;
import kr.co.solpick.card.dto.CardResponseDTO;
import kr.co.solpick.card.entity.Card;
import kr.co.solpick.card.entity.CardDesign;
import kr.co.solpick.card.repository.CardDesignRepository;
import kr.co.solpick.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardDesignService {
    private final CardDesignRepository cardDesignRepository;
    private final CardRepository cardRepository;

    // 배경 정보 저장
    @Transactional
    public Integer saveBackgroundInfo(Integer userId, Integer backgroundId) {
        CardDesign design = CardDesign.builder()
                .userId(userId)
                .backgroundId(backgroundId)
                .build();
        CardDesign savedDesign = cardDesignRepository.save(design);
        return savedDesign.getDesignId();
    }

    // 스티커 정보 저장
    @Transactional
    public void saveStickersInfo(Integer designId, String stickersData) {
        CardDesign design = cardDesignRepository.findById(designId)
                .orElseThrow(() -> new RuntimeException("디자인 정보를 찾을 수 없습니다."));
        design.setStickersData(stickersData);
        cardDesignRepository.save(design);
    }

    // 카드 발급 처리 (디자인 정보 + 개인 정보 결합)
    @Transactional
    public CardResponseDTO issueCard(CardIssueRequestDTO requestDTO) {
        Integer designId = requestDTO.getDesignId();

        // designId가 null이면 기본 디자인 ID로 설정
        if (designId == null) {
            designId = 1;
        }

        // 지정된 디자인 ID가 존재하지 않는 경우
        if (!cardDesignRepository.existsById(designId)) {
            // designId가 1이 아니면 로그 남기고 기본 디자인으로 변경
            if (designId != 1) {
                log.warn("지정된 디자인 ID가 존재하지 않습니다. designId: {}, 기본 디자인으로 대체합니다.", designId);
                designId = 1;
            }

            // 기본 디자인이 없으면 생성 (한 번만 검사하고 생성)
            if (!cardDesignRepository.existsById(1)) {
                createBasicDesign(requestDTO.getUserId());
            }
        }

        // 카드 발급 로직
        Card card = Card.builder()
                .userId(requestDTO.getUserId())
                .cardNumber(generateCardNumber())
                .cvcNumber(generateCvcNumber())
                .cardType("CREDIT")
                .designId(designId)
                .issueDate(LocalDate.now())
                .expiredAt(LocalDate.now().plusYears(5))
                .cardStatus("ACTIVE")
                .lastName(requestDTO.getLastName())
                .firstName(requestDTO.getFirstName())
                .build();

        Card savedCard = cardRepository.save(card);

        // 응답 반환 로직
        return CardResponseDTO.builder()
                .cardId(savedCard.getId())
                .userId(savedCard.getUserId())
                .cardNumber(maskCardNumber(savedCard.getCardNumber()))
                .expiredAt(savedCard.getExpiredAt().format(DateTimeFormatter.ofPattern("MM/yy")))
                .lastName(savedCard.getLastName())
                .firstName(savedCard.getFirstName())
                .build();
    }

    // 기본 디자인 생성 로직을 별도 메서드로 분리
    private CardDesign createBasicDesign(Integer userId) {
        CardDesign basicDesign = CardDesign.builder()
                .designId(1)
                .userId(userId)
                .backgroundId(1)
                .stickersData("[]")
                .build();

        CardDesign savedDesign = cardDesignRepository.save(basicDesign);
        log.info("기본 카드 디자인이 자동 생성되었습니다. userId: {}", userId);

        return savedDesign;
    }

    // 카드 정보 조회
    @Transactional
    public CardCompleteResponseDTO getCardInfo(Integer userId) {
        Card card = cardRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("카드 정보를 찾을 수 없습니다."));

        // 디자인 정보가 없거나 기본 디자인인 경우
        CardDesign design;
        if (card.getDesignId() == null || card.getDesignId() == 1) {
            // 기본 디자인 정보 반환
            design = CardDesign.builder()
                    .designId(1)
                    .backgroundId(1)
                    .stickersData("[]")
                    .build();
        } else {
            // 저장된 커스텀 디자인 정보 조회
            design = cardDesignRepository.findById(card.getDesignId())
                    .orElseThrow(() -> new RuntimeException("카드 디자인 정보를 찾을 수 없습니다."));
        }

        return CardCompleteResponseDTO.builder()
                .cardId(card.getId())
                .cardNumber(maskCardNumber(card.getCardNumber()))
                .cvcNumber(card.getCvcNumber())
                .expiryDate(card.getExpiredAt().format(DateTimeFormatter.ofPattern("MM/yy")))
                .lastName(card.getLastName())
                .firstName(card.getFirstName())
                .backgroundId(design.getBackgroundId())
                .stickersData(design.getStickersData())
                .build();
    }

    // 카드 번호 생성 (16자리)
    private String generateCardNumber() {
        // 카드 번호 생성 로직 (프로덕션에서는 보안 고려 필요)
        return "9411" + String.format("%012d", new Random().nextInt(999999999));
    }

    // CVC 번호 생성 (3자리)
    private String generateCvcNumber() {
        return String.format("%03d", new Random().nextInt(1000));
    }

    // 카드 번호 마스킹 (앞 4자리, 뒤 4자리만 표시)
    private String maskCardNumber(String cardNumber) {
        return cardNumber.substring(0, 4) + " **** **** " +
                cardNumber.substring(cardNumber.length() - 4);
    }
}
