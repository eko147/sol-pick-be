package kr.co.solpick.card.repository;

import kr.co.solpick.card.entity.CardDesign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardDesignRepository extends JpaRepository<CardDesign, Integer> {

    // 디자인 ID로 카드 디자인 찾기
    Optional<CardDesign> findById(Integer designId);

    // 특정 사용자의 카드 디자인 찾기
    Optional<CardDesign> findByUserId(Integer userId);
}
