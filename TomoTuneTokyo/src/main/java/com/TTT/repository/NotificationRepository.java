package com.TTT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.TTT.domain.Notification;
import java.util.List;

public interface NotificationRepository 
        extends JpaRepository<Notification, Long> {

    // 예: 특정 사용자의 읽지 않은 알림만 조회
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(String userId);
    
    //안읽은 알림 개수 리턴
    long countByUserIdAndIsReadFalse(String userId);


}