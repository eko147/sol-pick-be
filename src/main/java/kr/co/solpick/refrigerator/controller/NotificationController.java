package kr.co.solpick.refrigerator.controller;

import kr.co.solpick.refrigerator.dto.NotificationResponseDTO;
import kr.co.solpick.refrigerator.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solpick/noti")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 목록 조회
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(@PathVariable Long userId) {
        List<NotificationResponseDTO> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // 유통기한 알림 목록 조회
    @GetMapping("/list/expiration/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getExpirationNotifications(@PathVariable Long userId) {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsByType(userId, "expiration");
        return ResponseEntity.ok(notifications);
    }

    // 알림 읽음 처리
    @PatchMapping("/{notiId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notiId) {
        notificationService.markAsRead(notiId);
        return ResponseEntity.ok().build();
    }

    // 읽지 않은 알림 개수 조회
    @GetMapping("/count/unread/{userId}")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable Long userId) {
        Long unreadCount = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(unreadCount);
    }
}