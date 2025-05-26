package com.TTT.advice;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.TTT.domain.UserDto;
import com.TTT.security.CustomUserDetails;

@ControllerAdvice
public class CurrentUserAdvice {
	
	@ModelAttribute("currentUser")
	public UserDto currentUser(@AuthenticationPrincipal CustomUserDetails principal) {
		return principal != null ? principal.getUserDto() : null;
	}
}
