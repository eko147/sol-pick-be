package kr.co.solpick.game.repository;

import kr.co.solpick.game.entity.CompletedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompletedRecipeRepository extends JpaRepository<CompletedRecipe, Integer> {

    // 유저의 완성된 레시피 목록 조회
    List<CompletedRecipe> findByUserIdOrderByCompletionDateDesc(Integer userId);

    // 유저의 특정 레시피 완성 여부 확인
    boolean existsByUserIdAndRecipeId(Integer userId, Integer recipeId);
}