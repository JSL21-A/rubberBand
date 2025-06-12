package com.TTT.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TTT.domain.BandInsertVo;
import com.TTT.service.MypageService;
import com.TTT.service.UserService;

@RestController
@RequestMapping("/mypage")
public class MypageRestController {

	@Autowired
	MypageService mypageService;
	
	@Autowired
	UserService userService;
	
//	@Autowired
//	public MypageRestController(MypageService mypageService) {
//		this.mypageService = mypageService;
//	}
	
	@PostMapping("/leaveBand")
	public void leaveBand(
			@RequestParam("user_id") String user_id,
			@RequestParam("band_id") Long band_id
			) {
		mypageService.leaveMyBand(user_id, band_id);
		
//		ResponseEntity.ok();
	}
	
	//회원탈퇴
	@PostMapping("/deactivate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deactivateMember(Principal principal) {
	    // principal → 실제 user_id 추출
	    String userId = getCurrentUserId(principal);
	    boolean success = mypageService.deactivateMember(userId);

	    Map<String,Object> body = new HashMap<>();
	    body.put("success", success);
	    if (!success) {
	        body.put("message", "退会処理に失敗しました。");
	    }
	    return ResponseEntity.ok(body);
	}
		
	// 유저 아이디 추출 공통 함수
	private String getCurrentUserId(Principal principal) {
		String username = principal.getName();
		return userService.findByUsername(username).getUser_id();
	}
	
}
