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

    @Query("SELECT p.pointBalance FROM Point p WHERE p.userId = :userId ORDER BY p.createdAt DESC LIMIT 1")
    Optional<Integer> findLatestPointBalanceByUserId(@Param("userId") Integer userId);

    // 레시픽 회원 ID로 최신 포인트 잔액 조회
    @Query("SELECT p.pointBalance FROM Point p JOIN Member m ON p.userId = m.id WHERE m.recipickUserId = :recipickUserId ORDER BY p.createdAt DESC LIMIT 1")
    Optional<Integer> findLatestPointBalanceByRecipickUserId(@Param("recipickUserId") Integer recipickUserId);

    // 사용자 ID로 적립 포인트 합계 조회
    @Query("SELECT COALESCE(SUM(p.pointAmount), 0) FROM Point p WHERE p.userId = :userId AND p.pointType = 'EARN' AND p.pointAmount > 0")
    Integer findTotalEarnedPointsByUserId(@Param("userId") Integer userId);

    // 사용자 ID로 사용 포인트 합계 조회
    @Query("SELECT COALESCE(SUM(ABS(p.pointAmount)), 0) FROM Point p WHERE p.userId = :userId AND p.pointType = 'USE' AND p.pointAmount < 0")
    Integer findTotalUsedPointsByUserId(@Param("userId") Integer userId);

    // 사용자 ID로 포인트 내역 조회 (최신순)
    List<Point> findAllByUserIdOrderByCreatedAtDesc(Integer userId);
}