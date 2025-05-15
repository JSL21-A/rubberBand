package com.TTT.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

// 밴드 결성 등록

@Data
public class BandInsertVo {

	  // band_member 테이블
	  private Long band_member_id; // 멤버 고유 ID (자동증가)
	  private Long band_id; // Band ID
	  private String user_id; // User ID     
	  private MemberType member_type; // 멤버 유형 (리더 or 일반 멤버)
	  private String nickname; // 닉네임
	  private String member_position; // 포지션                      
	  private String member_mbti; // MBTI 성격 유형              
	  private String favorite_band; // 좋아하는 밴드                   
	  private String member_motto; // 한마디                      
	  private LocalDateTime created_at; // 등록 일시
	  private String user_img; // 프로필 사진 (user 테이블에 있는 프로필 사진 참조)
	  private Long user_profile_id;
	  private String stage_name; // 활동명
	  private String photo; 
	  
	  // bands 테이블
	  private String  band_name; // 밴드 이름
	  private String  band_intro; // 밴드 소개문
	  private LocalDate formation_date; // 결성일
	  private String  region; // 활동지역
	  private String  band_profile_img; // 밴드 프로필 이미지
	  private String  band_cover_img; // 밴드 커버 이미지
	  private int   like_count; // 좋아요
	  private String youtube_link; // 유튜브 링크
	  private String instagram_link; // 인스타그램 링크
	  private String leader_comment; // 리더 코멘트
	  
	  // band_tags 테이블
	  private Long tag_id; // 태그 고유 ID (자동증가)
	  private  String tag_type; // 태그 유형
	  private  String tag_value; // 실제 태그 내용
	  
	  // 태그 선택 결과들을 각각 저장하기 위한 필드
	  private String selectedGenres;
	  private String selectedPositions;
	  private String selectedGenders;
	  private String selectedAges;

	  // 페이징
	  private int band_count;

	  
}
