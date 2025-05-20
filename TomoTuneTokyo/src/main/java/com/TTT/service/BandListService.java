package com.TTT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TTT.domain.BandInsertVo;
import com.TTT.domain.BandListInsertVo;
import com.TTT.mapper.BandListMapper;

@Service
public class BandListService {
	
	@Autowired
	private BandListMapper BandListMapper;
	
	private final String uploadDir = "C:\\Users\\LG gram\\git\\rubberBand\\TomoTuneTokyo\\src\\main\\resources\\static\\images\\uploads\\bands\\"; // 이미지 저장 경로

	// 구인구직 등록
	public void bandInsert(BandListInsertVo vo) {
		
	}
}
