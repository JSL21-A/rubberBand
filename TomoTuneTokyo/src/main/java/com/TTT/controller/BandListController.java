package com.TTT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;




@Controller
@RequestMapping("/bandlist")
public class BandListController {

	@GetMapping("/view")
	public String viewPortfolio() {
		
		return "/bandlist/view";
	}
	
	@GetMapping("/list")
	public String portList() {
		return "/bandlist/list";
	}
}
