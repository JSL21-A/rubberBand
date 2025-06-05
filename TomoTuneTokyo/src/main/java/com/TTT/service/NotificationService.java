package com.TTT.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.TTT.domain.Notification;
import com.TTT.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;
    // userId → emitter 맵
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Autowired
    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    /**
     * 클라이언트가 /subscribe?userId=xxx 로 연결하면 여기를 타고 SseEmitter를 반환합니다.
     */
    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(()    -> emitters.remove(userId));
        return emitter;
    }

    /**
     * 알림을 생성하고 저장한 뒤, 해당 userId로 실시간 푸시까지 수행합니다.
     */
    public void sendNotification(String userId, String category, String message, String url) {
        // 1) 엔티티 생성 & 저장
        Notification n = Notification.builder()
            .userId(userId)
            .category(category)
            .message(message)
            .url(url)
            .build();
        Notification saved = notificationRepo.save(n);

        // 2) 실시간 푸시
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(
                    SseEmitter.event()
                              .id(String.valueOf(saved.getNotificationId()))
                              .name("notification")
                              .data(saved)
                );
            } catch (Exception ex) {
                // 문제가 생기면 맵에서 제거
                emitters.remove(userId);
            }
        }
    }

    /**
     * 아직 읽지 않은 알림만 조회
     */
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepo.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * 특정 알림을 읽음 처리
     */
    public void markAsRead(Long notificationId) {
        notificationRepo.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepo.save(n);
        });
    }
    
    //안읽은 알림 개수 조회하기
    public long countUnreadNotifications(String userId) {
        return notificationRepo.countByUserIdAndIsReadFalse(userId);
    }

}