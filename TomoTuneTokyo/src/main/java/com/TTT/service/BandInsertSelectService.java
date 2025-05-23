package com.TTT.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TTT.domain.BandInsertVo;
import com.TTT.mapper.BandInsertSelectMapper;

// 밴드 결성 후 밴드 상세 조회
@Service
public class BandInsertSelectService {

	@Autowired
	private BandInsertSelectMapper bandinsertselectMapper;

	private final String uploadDir = "C:\\Users\\LG gram\\git\\rubberBand\\TomoTuneTokyo\\src\\main\\resources\\static\\images\\uploads\\bands\\"; // 이미지 저장 경로
	
	  // 밴드 기본 정보 조회
    public BandInsertVo getBandDetail(String bandId) {
        return bandinsertselectMapper.selectBandDetail(bandId);
    }

    // 밴드 멤버 정보 조회
    public List<BandInsertVo> getBandMembers(String bandId) {
        return bandinsertselectMapper.selectBandMembers(bandId);
    }

    // 밴드 태그 정보 조회
    public List<BandInsertVo> getBandTags(String bandId) {
        return bandinsertselectMapper.selectBandTags(bandId);
    }
    
    // 리더 이메일 조회
    public String getLeaderEmail(String bandId) {
        String userId = bandinsertselectMapper.selectLeaderUserId(bandId);
        return bandinsertselectMapper.selectEmailByUserId(userId);
    }

    // 리더 정보 조회
    public BandInsertVo selectLeaderInfo(String bandId) {
        return bandinsertselectMapper.selectLeaderInfo(bandId);
    }

}
