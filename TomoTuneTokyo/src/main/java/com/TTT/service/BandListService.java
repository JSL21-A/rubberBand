package com.TTT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TTT.mapper.BandListMapper;

@Service
public class BandListService {
	
	@Autowired
	private BandListMapper BandListMapper;
	
	

}
