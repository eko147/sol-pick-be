package kr.co.solpick.card.repository;

import kr.co.solpick.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

    /**
     * 사용자 ID로 카드 조회 (사용자당 카드가 하나인 경우)
     */
    Optional<Card> findByUserId(Integer userId);

    /**
     * 사용자 ID, 카드 번호, 카드 상태로 카드 정보 조회
     */
    Optional<Card> findByUserIdAndCardNumberAndCardStatus(Integer userId, String cardNumber, String cardStatus);

    boolean existsByUserId(Integer userId);
}