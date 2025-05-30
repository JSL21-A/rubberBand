package com.TTT.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	// username을 기반으로 user_id(UUID) 조회
	public String findUserIdByUsername(String username) {
		return bandRecruitPostSelectMapper.findUserIdByUsername(username);
	}
	
	// 모든 이력서 ID 조회 (로그인 사용자 UUID 기준)
	public List<Long> getAllResumeIdsByUserId(String userId) {
	    return bandRecruitPostSelectMapper.getAllResumeIdsByUserId(userId);
	}
	
	// 지원하기
	public void insertApplication(Long postId, String userId, Long resumeId, Long bandId) {
		Map<String, Object> params = new HashMap<>();
		params.put("postId", postId);
		params.put("userId", userId);
		params.put("resumeId", resumeId);
		params.put("bandId", bandId);

		bandRecruitPostSelectMapper.insertApplication(params);
	}
	
	// 지원 중복 방지 (한 band_id 당 한 번만 지원가능)
	public boolean hasAlreadyApplied(Long postId, String userId) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("postId", postId);
	    params.put("userId", userId);
	    return bandRecruitPostSelectMapper.countExistingApplication(params) > 0;
	}

	// 스크랩 중복 확인 
	public boolean hasAlreadyScrapped(Long postId, String userId) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("postId", postId);
	    params.put("userId", userId);
	    int count = bandRecruitPostSelectMapper.countScrapByUserAndPost(params);
	    return count > 0;
	}

	// 스크랩 추가 
	public void insertScrap(Long postId, String userId) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("postId", postId);
	    params.put("userId", userId);
	    bandRecruitPostSelectMapper.insertScrap(params);
	}

	// 스크랩 해제
	public void deleteScrap(Long postId, String userId) {
	    Map<String, Object> param = new HashMap<>();
	    param.put("postId", postId);
	    param.put("userId", userId);
	    bandRecruitPostSelectMapper.deleteScrap(param);
	}


}
