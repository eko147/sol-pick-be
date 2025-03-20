package kr.co.solpick.refrigerator.repository;

import kr.co.solpick.refrigerator.entity.UserAllergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAllergyRepository extends JpaRepository<UserAllergy, Integer> {
    List<UserAllergy> findByUserId(Integer userId);
    void deleteByUserIdAndIngredientName(Integer userId, String ingredientName);
}

