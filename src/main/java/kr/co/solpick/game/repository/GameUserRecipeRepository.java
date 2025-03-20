package kr.co.solpick.game.repository;

import kr.co.solpick.game.entity.GameUserRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameUserRecipeRepository extends JpaRepository<GameUserRecipe, Integer> {

    // 유저가 가장 최근에 선택한 레시피 조회
    Optional<GameUserRecipe> findTopByUserIdOrderBySelectedAtDesc(Integer userId);

    // 유저의 특정 레시피 선택 여부 확인
    boolean existsByUserIdAndGameRecipeId(Integer userId, Integer gameRecipeId);
}