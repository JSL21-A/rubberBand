package com.TTT.controller;
 
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.TTT.domain.MypageDto;
import com.TTT.domain.UserDto;
import com.TTT.service.MypageService;
import com.TTT.service.UserService;

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
	    return "mypage/resumeInsert";
	}

	// 이력서 저장 

	@Autowired
	private UserService userService;

	@PostMapping("/insert")
	public String insertResume(@ModelAttribute MypageDto dto, Principal principal) {
	    String username = principal.getName();

	    UserDto user = userService.findByUsername(username);
	    String userId = user.getUser_id();

	    dto.setUserId(userId);

	    mypageService.saveResume(dto);
	    return "redirect:/resume/insert"; //경로 바꾸기 !!
	}


}
