package kr.co.solpick.game.repository;

import kr.co.solpick.game.entity.DiscoveredIngredients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscoveredIngredientsRepository extends JpaRepository<DiscoveredIngredients, Integer> {

    // 유저의 레시피별 발견한 식재료 목록 조회
    List<DiscoveredIngredients> findByUserIdAndRecipeId(Integer userId, Integer recipeId);

    // 유저의 특정 레시피와 특정 식재료 조회
    Optional<DiscoveredIngredients> findByUserIdAndRecipeIdAndIngredientName(
            Integer userId, Integer recipeId, String ingredientName);

    // 유저의 특정 레시피 식재료 삭제 (초기화용)
    void deleteByUserIdAndRecipeId(Integer userId, Integer recipeId);
}