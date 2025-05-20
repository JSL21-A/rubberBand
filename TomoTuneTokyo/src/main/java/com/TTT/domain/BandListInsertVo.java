package com.TTT.domain;

import java.time.LocalDateTime;


import lombok.Data;

// 밴드 결성 등록

@Data
public class BandListInsertVo {

	// POSTS 테이블 매핑
    private String post_id;             // 게시글 ID
    private String board_id;            // 게시판 구분 ID
    private String user_id;             // 작성자 ID
    private String post_title;          // 제목
    private String post_content;        // 본문 (소개 + 조건 등)
    private String post_img;            // 대표 이미지 파일명
    private String post_status;         // 게시 상태 ('A': 활성 등)
    private int post_like;              // 좋아요 수
    private LocalDateTime created_at;   // 작성일
    private LocalDateTime updated_at;   // 수정일

    // 추가 필드 (입력 폼 확장값)
    private String activity_area;       // 활동 지역
    private String recruit_position;    // 모집 포지션
    private String recruit_condition;   // 모집 조건
    private String preferred_genres;    // 선호 장르
    private String leader_comment;      // 리더 코멘트
    private String deadline;            // 마감일
    private String tags;                // 추천 태그

    // 다중 이미지 업로드용
    private String band_images;  // 활동 사진 (최대 4장 업로드)

	  
}
