package kr.co.solpick.refrigerator.repository;

import kr.co.solpick.refrigerator.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    // 최신순 조회
    List<Ingredient> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 등록순 조회
    List<Ingredient> findByUserIdOrderByCreatedAtAsc(Long userId);

    // 유통기한 임박순 조회
    List<Ingredient> findByUserIdOrderByExpiryDateAsc(Long userId);

    // 식재료명 가나다순 조회
    List<Ingredient> findByUserIdOrderByNameAsc(Long userId);
}