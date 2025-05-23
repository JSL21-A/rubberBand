package com.TTT.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.TTT.domain.UserDto;
import com.TTT.service.NotificationService;
import com.TTT.service.UserService;



@Controller
public class MainController {
	
	@Autowired
	UserService userService;
	
	//테스트용
	@Autowired
	NotificationService notificationService;

	
	//index.html
	@GetMapping("/")
	public String Home(Model model) {
		model.addAttribute("isIndex", true);
		return ("index");
	}
	
	//로그인시 header fragment만 새로고침
	@GetMapping("/user/header-fragment")
	public String headerFragment(Model model, Authentication auth) {
		UserDto user = userService.findByUsername(auth.getName());
		model.addAttribute("currentUserId", user.getUser_id());
		return "fragments/header :: header";
	}
	
	//신규 채팅 생성시 chats fragment만 새로고침 
	@GetMapping("/user/chats-fragment")
	public String chatsFragment() {
		return "fragments/chats :: chats";
	}
	

	
	
}
