package com.TTT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bandselect")
public class BandSelectController {
	
	// 밴드 상세정보
		@GetMapping("/modify")
		public String modify() {
			return "/band/modify";
		}
		
}