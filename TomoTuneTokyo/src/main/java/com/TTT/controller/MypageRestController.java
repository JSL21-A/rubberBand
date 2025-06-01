package com.TTT.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TTT.domain.BandInsertVo;
import com.TTT.service.MypageService;

@RestController
@RequestMapping("/mypage")
public class MypageRestController {

	@Autowired
	MypageService mypageService;
	
//	@Autowired
//	public MypageRestController(MypageService mypageService) {
//		this.mypageService = mypageService;
//	}
	
	@PostMapping("/leaveBand")
	public void leaveBand(
			@RequestParam("user_id") String user_id,
			@RequestParam("band_id") Long band_id
			) {
		mypageService.leaveMyBand(user_id, band_id);
		
//		ResponseEntity.ok();
	}
	
}
