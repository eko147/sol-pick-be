package kr.co.solpick.refrigerator.repository;

import kr.co.solpick.refrigerator.entity.ExpirationNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpirationNotificationRepository extends JpaRepository<ExpirationNotification, Long> {

}