package com.TTT.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BandActiveInsertVo {

	// 밴드 정보 상세보기에 있는 활동사진 영역
	private Long activityPhotoId; // 활동 사진 고유 ID
	private Long bandId; // 밴드 ID (시퀀스 기반)
	private String userId; // 등록한 사용자 ID (리더 확인용)
	private String imageUrl; // 사진 파일 경로
	private String activityYoutubeUrl; // 유튜브 영상 URL
	private String activityTitle; // 활동 제목
	private String activityContent; // 활동 설명
	private LocalDateTime createdAt; // 생성일
	private LocalDateTime updatedAt; // 수정일

	
	// 밴드 정보 상세보기에 있는 활동기록 영역
	private Long recordId; // 활동 기록 ID (PK)
	private String recordTitle; // 공연 제목
	private LocalDate recordDate; // 공연 날짜
	private String recordTime; // 공연 시간
	private String recordLocation; // 공연 장소
	private Integer participantCount; // 총 인원
	private Integer currentCount; // 현재 인원
	private String recordStatus; // 상태 (예정, 종료 등)
	private String recordDescription; // 상세 설명
	private String recordImageUrl; // 썸네일 이미지 경로

	// 뷰에서 사용할 날짜 포맷 (예: "5월")
	private String formattedDate;
	private String formattedDay;
	
}
