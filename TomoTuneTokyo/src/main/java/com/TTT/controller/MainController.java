package com.TTT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	@GetMapping("/")
	public String headerTest(Model model) {
		model.addAttribute("isIndex", true);
		return ("index");
	}

}
