package kr.co.solpick.refrigerator.repository;

import kr.co.solpick.refrigerator.entity.RefrigeratorIng;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefrigeratorIngRepository extends JpaRepository<RefrigeratorIng, Integer> {
    // 특정 사용자(user_id)의 냉장고 식재료 목록 조회
    List<RefrigeratorIng> findByUserId(Integer userId);
}
