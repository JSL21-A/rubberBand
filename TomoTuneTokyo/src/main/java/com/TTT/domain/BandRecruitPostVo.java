package com.TTT.domain;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class BandRecruitPostVo {

	private Long post_id; // 게시글 ID
	private Long band_id; // 밴드 ID
	private String userId; // 작성자 ID

	private String band_intro; // 밴드 소개
	private String title; // 모집 제목
	private String recruitPosition; // 모집 포지션
	private String activityArea; // 활동 지역
	private String recruitCondition; // 모집 조건
	private String preferredGenres; // 선호 장르
	private String leaderComment; // 리더의 코멘트
	private Date deadline; // 모집 마감일
	private String tagKeywords; // 추천 태그

	private String image1Url; // 활동 사진 1
	private String image2Url; // 활동 사진 2
	private String image3Url; // 활동 사진 3
	private String image4Url; // 활동 사진 4

	private Timestamp createdAt; // 생성일
	private Timestamp updatedAt; // 수정일

	// 추천 태그
	private Long tag_id; // 태그 고유 ID (자동증가)
	private String tag_type; // 태그 유형
	private String tag_value; // 실제 태그 내용

	private String  band_name; // 밴드 이름
	
	private Long bandrecruitpost_count; // 조회수
	
	
}
