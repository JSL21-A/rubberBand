package com.TTT.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.TTT.domain.MypageDto;
import com.TTT.service.MypageService;

@Controller
@RequestMapping("/resume")
public class MypageController {
	
	private final MypageService mypageService;

	public MypageController(MypageService mypageService) {
		this.mypageService = mypageService;
	}

	// 이력서 입력 폼 열기
	@GetMapping("/insert")
	public String resumeInsertForm() {
	    return "mypage/resumeInsert"; // templates/mypage/resumeInsert.html
	}

	// 이력서 저장 
	@PostMapping("/insert")
	public String insertResume(@ModelAttribute MypageDto dto,
	                           Principal principal) {
	    // 로그인한 사용자 ID를 불러오기 
	    String userId = principal.getName();
	    dto.setUserId(userId); // DTO에 설정

	    // 저장 처리
	    mypageService.saveResume(dto);

	    return "redirect:/mypage/success"; // 저장 후 이동할 페이지
	}
}
