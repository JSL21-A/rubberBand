package com.TTT.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.TTT.domain.UserDto;
import com.TTT.service.UserService;

@ControllerAdvice
public class GlobalControllerAdvice {
	
	@Autowired private UserService userService;
	
	@ModelAttribute("currentUserId")
	public String currentUserId(Authentication auth) {
		if(auth != null
				&& auth.isAuthenticated()
				&& !(auth instanceof AnonymousAuthenticationToken)) {
			UserDto user = userService.findByUsername(auth.getName());
			return user.getUser_id();
		}
		return null; //anonymous일 때
	}

}
