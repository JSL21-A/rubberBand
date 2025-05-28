package com.TTT.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TTT.service.BandInsertSelectService;


@RestController
@RequestMapping("/bandinsert")
public class BandRestController {
	
	@Autowired
	BandInsertSelectService bandInsertSelectService;
	
	
	//밴드 초대 수락
	@PostMapping("/{band_id}/invite/{user_id}/accept")
	public ResponseEntity<Void> acceptInvite(
			@PathVariable("band_id") Long band_id,
			@PathVariable("user_id") String user_id
			){
		bandInsertSelectService.acceptBandInvite(user_id, band_id);
		return ResponseEntity.ok().build();
	}

	//밴드 초대 거절
	@PostMapping("/{band_id}/invite/{user_id}/decline")
	public ResponseEntity<Void> declineInvite(
			@PathVariable("band_id") Long band_id,
			@PathVariable("user_id") String user_id
			){
		bandInsertSelectService.declineBandInvite(user_id, band_id);
		return ResponseEntity.ok().build();
	}
	
	
	
	
}
