package com.TTT.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.TTT.domain.UserDto;
import com.TTT.service.UserService;



@Controller
public class MainController {
	
	@Autowired
	UserService userService;

	
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
	
	
}
