package com.TTT.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.TTT.domain.UserDto;
import com.TTT.security.CustomUserDetails;
import com.TTT.service.MailService;
import com.TTT.service.MypageService;
import com.TTT.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserService userService;
	@Autowired
	MailService mailService;
	@Autowired
	SpringTemplateEngine templateEngine;
	
	//ID(username) 중복 체크
	@GetMapping("/check-username")
	public ResponseEntity<?> checkUsernameExists(@RequestParam("username") String username){
		return ResponseEntity.ok(!userService.usernameExists(username));
	}
	
	//닉네임 중복 체크
	@GetMapping("/check-nickname")
	public ResponseEntity<?> checkNicknameExists(@RequestParam("nickname") String nickname){
		return ResponseEntity.ok(!userService.nicknameExists(nickname));
	}
	
	//회원가입 인증 이메일 전송
	@PostMapping("/send-email-code")
	public ResponseEntity<Void> sendEmailCode(
			@RequestParam("email") String email,
			HttpSession session
	) {
		String code = mailService.sendEmail(email);
		session.setAttribute("emailAuthCode", code);
		session.setMaxInactiveInterval(5 * 60);
		
		return ResponseEntity.ok().build();
	}
	
	//인증 코드 확인
	@PostMapping("/verify-email-code")
	public ResponseEntity<Boolean> verifyCode(
			@RequestParam("code") String code,
			HttpSession session
	) {
		String codeSaved = (String) session.getAttribute("emailAuthCode");
		boolean ok = codeSaved != null && codeSaved.equals(code);
		
		if(ok) {
			session.removeAttribute("emailAuthCode");
		}
		return ResponseEntity.ok(ok);
	}
	
	//회원가입 유저정보 등록
	@PostMapping("/register")
	public ResponseEntity<?> register(UserDto userDto){
		userService.userInsert(userDto);
		return ResponseEntity.ok().build();
	}
	
	//비밀번호 초기화
	@PostMapping("/reset-password")
	public ResponseEntity<Void> resetPassword(@RequestBody Map<String, String> body){
		String username = body.get("username");
		String newPassword = body.get("newPassword");
		userService.updatePassword(username, newPassword);
		return ResponseEntity.ok().build();
	}
	
	//ID(username) 존재여부 확인
	@GetMapping("/check-username-reset")
	public ResponseEntity<?> checkUsernameExistsReset(@RequestParam("username") String username){
		return ResponseEntity.ok(userService.usernameExists(username));
	}
	
	//아이디, 이메일 일치여부 확인
	@GetMapping("/check-username-email")
	public ResponseEntity<Boolean> checkUsernameEmail(
			@RequestParam("username") String username,
			@RequestParam("email") String email
			){
		boolean ok = userService.isUsernameEmailMatch(username, email);
		return ResponseEntity.ok(ok);
	}
	
	//비밀번호 검증
	@PostMapping("/verify-current-password")
	public ResponseEntity<Map<String, Object>> verifyCurrentPassword(
			@RequestBody Map<String, String> body,
			@AuthenticationPrincipal CustomUserDetails principal){
		String currentPassword = body.get("currentPassword");
		String userId = principal.getUserDto().getUser_id();
		
		boolean ok = userService.checkPassword(userId, currentPassword);
		return ResponseEntity.ok(Collections.singletonMap("ok", ok));
	}
	
	//이메일 업데이트
	@PostMapping("/update-email")
	public ResponseEntity<Map<String, Object>> updateEmail(
	        @RequestParam("user_id") String userId,
	        @RequestParam("new_email") String newEmail,
	        Principal principal
			) {

	    boolean success = userService.updateEmail(userId, newEmail);

	    Map<String, Object> responseBody = new HashMap<>();
	    responseBody.put("success", success);
	    if (!success) {
	        responseBody.put("message", "メール更新に失敗しました。");
	    }
	    
	    String username = principal.getName();
	    if (success) {
	    	UserDto updatedUser = userService.findByUsername(username);
	    	Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
	    	Collection<? extends GrantedAuthority> roles = currentAuth.getAuthorities();
	    	CustomUserDetails newDetails = new CustomUserDetails(updatedUser);
	    	
	    	UsernamePasswordAuthenticationToken newAuth = 
	    			new UsernamePasswordAuthenticationToken(newDetails, currentAuth.getCredentials(), roles);
	    	SecurityContextHolder.getContext().setAuthentication(newAuth);
	    }

	    return ResponseEntity
	            .ok()
	            .header("Content-Type", "application/json")
	            .body(responseBody);
	}
	
	
	

	
	
	

}
