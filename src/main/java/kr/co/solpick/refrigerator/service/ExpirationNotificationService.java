package kr.co.solpick.refrigerator.service;

import kr.co.solpick.member.repository.MemberRepository;
import kr.co.solpick.refrigerator.entity.ExpirationNotification;
import kr.co.solpick.refrigerator.entity.Ingredient;
import kr.co.solpick.refrigerator.entity.Notification;
import kr.co.solpick.refrigerator.repository.ExpirationNotificationRepository;
import kr.co.solpick.refrigerator.repository.IngredientRepository;
import kr.co.solpick.refrigerator.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpirationNotificationService {

//    매일 오전 9시 30분에 실행
//    유통기한이 3일 이내로 남은 식재료와 이미 지난 식재료 탐색
//    임박한 식재료와 만료된 식재료를 그룹화하여 각각 알림 생성
//    메시지 형식은 식재료 개수에 따라 상이 (단일 vs 여러 개)
//    noti 테이블과 expiration_noti 테이블 모두에 새로운 데이터 저장

    private final IngredientRepository ingredientRepository;
    private final NotificationRepository notificationRepository;
    private final ExpirationNotificationRepository expirationNotificationRepository;
    private final MemberRepository memberRepository;

    // 매일 오전 9시 30분에 실행
    @Scheduled(cron = "0 30 9 * * ?", zone = "Asia/Seoul")
    @Transactional
    public void checkExpirationDates() {
        LocalDateTime now = LocalDateTime.now();
        log.info("🟢 유통기한 알림 검사 시작: {}", now);

        // 유저 ID 목록 가져오기
        List<Long> userIds = memberRepository.findAllMemberIds().stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        for (Long userId : userIds) {
            // (최신순 조회로) 사용자의 모든 식재료 가져오기
            List<Ingredient> userIngredients = ingredientRepository.findByUserIdOrderByCreatedAtDesc(userId);

            // 유통기한 임박 식재료 필터링 (0~3일)
            List<Ingredient> upcomingExpirations = userIngredients.stream()
                    .filter(ingredient -> {
                        if (ingredient.getExpiryDate() == null) return false;
                        long daysUntilExpiry = ChronoUnit.DAYS.between(
                                now.toLocalDate(), ingredient.getExpiryDate().toLocalDate());
                        return daysUntilExpiry >= 0 && daysUntilExpiry <= 3;
                    })
                    .collect(Collectors.toList());

            // 유통기한 만료 식재료 필터링
            List<Ingredient> expiredIngredients = userIngredients.stream()
                    .filter(ingredient -> {
                        if (ingredient.getExpiryDate() == null) return false;
                        return ingredient.getExpiryDate().toLocalDate().isBefore(now.toLocalDate());
                    })
                    .collect(Collectors.toList());

            log.info("🟢 사용자 {}: 임박 식재료 {}개, 만료 식재료 {}개",
                    userId, upcomingExpirations.size(), expiredIngredients.size());

            // 임박 알림 생성
            if (!upcomingExpirations.isEmpty()) {
                createGroupedExpirationNotification(userId, upcomingExpirations, false);
            }

            // 만료 알림 생성
            if (!expiredIngredients.isEmpty()) {
                createGroupedExpirationNotification(userId, expiredIngredients, true);
            }
        }

        log.info("🟢 유통기한 알림 검사 완료");
    }

    // 그룹화된 유통기한 알림 생성 (여러 식재료를 하나의 알림으로 묶음)
    private void createGroupedExpirationNotification(
            Long userId, List<Ingredient> ingredients, boolean isExpired) {

        String message;

        if (ingredients.size() == 1) {
            // 단일 식재료 메시지
            Ingredient ingredient = ingredients.get(0);
            if (isExpired) {
                message = ingredient.getName() + "의 유통기한이 지났어요!";
            } else {
                long daysLeft = ChronoUnit.DAYS.between(
                        LocalDateTime.now().toLocalDate(),
                        ingredient.getExpiryDate().toLocalDate());

                if (daysLeft == 0) {
                    message = ingredient.getName() + "의 유통기한이 오늘까지에요!";
                } else {
                    message = ingredient.getName() + "의 유통기한이 " + daysLeft + "일 남았어요!";
                }
            }
        } else {
            // 여러 식재료 메시지 (최대 3개 이름 표시, 나머지는 숫자로 표시)
            String names = ingredients.stream()
                    .map(Ingredient::getName)
                    .limit(3)
                    .collect(Collectors.joining(", "));

            if (ingredients.size() > 3) {
                names += " 외 " + (ingredients.size() - 3) + "개";
            }

            if (isExpired) {
                message = names + "의 유통기한이 지났어요!";
            } else {
                message = names + "의 유통기한이 얼마 남지 않았어요!";
            }
        }

        log.info("🟢 알림 메시지 생성: {}", message);

        // 알림 엔티티 생성 및 저장
        Notification notification = Notification.builder()
                .userId(userId)
                .type("expiration")
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        // 각 식재료에 대한 상세 알림 정보 생성
        for (Ingredient ingredient : ingredients) {
            ExpirationNotification expirationNotification = ExpirationNotification.builder()
                    .ingredientId(ingredient.getId())
                    .notificationId(savedNotification.getId())
                    .notificationType(isExpired ?
                            ExpirationNotification.TYPE_EXPIRED :
                            ExpirationNotification.TYPE_UPCOMING)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            expirationNotificationRepository.save(expirationNotification);
        }
    }
}