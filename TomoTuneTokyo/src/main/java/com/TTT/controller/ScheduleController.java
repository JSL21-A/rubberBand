package com.TTT.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TTT.domain.ScheduleDto;
import com.TTT.service.ScheduleService;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
	private final ScheduleService service;
	
	public ScheduleController(ScheduleService service) {
		this.service = service;
	}
	
	@GetMapping
	public Map<String, List<ScheduleDto>> monthly(
			@RequestParam int year,
			@RequestParam int month){
		return service.getMonthly(year, month);
	}
}
