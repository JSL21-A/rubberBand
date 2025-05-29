package com.TTT.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TTT.domain.BandRecruitPostVo;
import com.TTT.mapper.BandRecruitPostSelectMapper;

@Service
public class BandRecruitPostSelectService {

	@Autowired
	private BandRecruitPostSelectMapper bandRecruitPostSelectMapper;

	private final String uploadDir = "C:\\Users\\LG gram\\git\\rubberBand\\TomoTuneTokyo\\src\\main\\resources\\static\\images\\uploads\\bands\\"; // 이미지
																																					// 저장
																																					// 경로
	// 특정 게시글 ID로 밴드 정보를 조회

	public BandRecruitPostVo getPostById(Long postId) {
		return bandRecruitPostSelectMapper.getPostById(postId);
	}

	// band_name
	public String getBandName(Long bandId) {
		return bandRecruitPostSelectMapper.selectBandName(bandId);
	}

	// 조회수 증가
	public void incrementViewCount(Long postId) {
	    bandRecruitPostSelectMapper.incrementViewCount(postId);
	}

	// 조회수 조회
	public Long getViewCount(Long postId) {
	    return bandRecruitPostSelectMapper.getViewCount(postId);
	}
	
	// 추천 태그
	public List<String> getRecommendedTags(Long postId) {
	    String tagStr = bandRecruitPostSelectMapper.getTagKeywords(postId);
	    if (tagStr != null && !tagStr.trim().isEmpty()) {
	        return Arrays.asList(tagStr.trim().split(" ")); // 공백으로 분리
	    } else {
	        return new ArrayList<>();
	    }
	}




}
