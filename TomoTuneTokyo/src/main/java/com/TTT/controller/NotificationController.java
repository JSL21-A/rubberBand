package com.TTT.controller;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.TTT.domain.Notification;
import com.TTT.security.CustomUserDetails;
import com.TTT.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {
	
	@Autowired
	NotificationService notificationService;
	
	//알림 연결
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String userId = customUserDetails.getUserDto().getUser_id();
        return notificationService.subscribe(userId);
    }

	//테스트용 로그아웃 알림
	@GetMapping("/test/notify-logout")
	public ResponseEntity<Void> testLogoutNotify(Authentication auth){
		String userId = auth.getName();
		notificationService.sendNotification(userId, "test", "로그아웃되었습니다.(테스트)", "/");
		return ResponseEntity.ok().build();
		
	}
	
	//알림 읽음 처리
	@PostMapping("/read/{id}")
	public ResponseEntity<Void> markAsRead(@PathVariable("id") Long id){
		notificationService.markAsRead(id);
		return ResponseEntity.noContent().build();
	}
	
	//안읽은 알림 개수 조회
	@GetMapping("/unread/count")
	public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        String userId = principal.getUserDto().getUser_id();
        long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(count);
    }
	
	
	//읽지 않은 알림 불러오기
	@GetMapping("/unread")
	public ResponseEntity<List<Notification>> getUnread(
			@AuthenticationPrincipal CustomUserDetails customUserDetails
			){
		String user_id = customUserDetails.getUserDto().getUser_id();
		return ResponseEntity.ok(
				notificationService.getUnreadNotifications(user_id)
				);
	}
}
