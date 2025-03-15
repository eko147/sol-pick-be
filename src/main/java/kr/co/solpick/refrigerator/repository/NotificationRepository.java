package kr.co.solpick.refrigerator.repository;

import kr.co.solpick.refrigerator.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 알림 목록 조회 (최신순)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 타입별 알림 목록 조회
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);

    // 읽지 않은 알림 개수 조회
    Long countByUserIdAndIsReadFalse(Long userId);
}