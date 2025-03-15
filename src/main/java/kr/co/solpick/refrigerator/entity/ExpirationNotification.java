package kr.co.solpick.refrigerator.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "expiration_noti")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpirationNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expiration_noti_id")
    private Long id;

    @Column(name = "refrigerator_ing_id")
    private Long ingredientId;

    @Column(name = "noti_id")
    private Long notificationId;

    @Column(name = "expiration_noti_type")
    private String notificationType; // "UPCOMING" 또는 "EXPIRED"

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 알림 타입 상수
    public static final String TYPE_UPCOMING = "UPCOMING"; // 임박 알림
    public static final String TYPE_EXPIRED = "EXPIRED";   // 만료 알림
}