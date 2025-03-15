package kr.co.solpick.point.repository;

import kr.co.solpick.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Integer> {

    // 특정 사용자의 최신 포인트 잔액 조회
    @Query("SELECT p.pointBalance FROM Point p WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    Optional<Integer> findLatestPointBalanceByUserId(@Param("userId") Integer userId);

    // 레시픽 회원 ID(recipick_user_id)로 최신 포인트 잔액 조회
    @Query("SELECT p.pointBalance FROM Point p JOIN Member m ON p.userId = m.id WHERE m.recipickUserId = :recipickUserId ORDER BY p.createdAt DESC")
    Optional<Integer> findLatestPointBalanceByRecipickUserId(@Param("recipickUserId") Integer recipickUserId);
}