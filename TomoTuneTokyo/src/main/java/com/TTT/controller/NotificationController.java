package com.TTT.controller;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.TTT.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {
	
	@Autowired
	NotificationService notificationService;
	
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(Authentication auth) {
        String userId = auth.getName();
        return notificationService.subscribe(userId);
    }

	
//	@GetMapping("/test/notify-logout")
//	public ResponseEntity<Void> testLogoutNotify(Authentication auth){
//		String userId = auth.getName();
//		notificationService.sendNotification(userId, "test", "로그아웃되었습니다.(테스트)", "/");
//		return ResponseEntity.ok().build();
//		
//	}
}
