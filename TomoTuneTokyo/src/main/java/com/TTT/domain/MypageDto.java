package com.TTT.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data

public class MypageDto {

	// resume 
	private int resumeId; // 이력서 ID
	private String userId; // 유저 아이디
	private LocalDateTime createdAt; // 등록일시 
	private LocalDateTime updatedAt; // 수정일시 
	// 프로필
	private String stageName; // 활동명
	private String gender; // 성별
	private String birth; // 생년월일
	private String sns; // 이메일
	// 합주정보
	private String area; // 활동지역
	private String instrument; // 밴드포지션
	private String genre; // 선호장르
	private String practiceDate; //연습가능요일     
	private String practiceTime; // 연습가능시간대
	private String detailTime; // 구체적인 시간
	// 자유입력
	private String note; // 자기소개
	// BandHistoryDto 불러오기
	private List<BandHistoryDto> bandHistoryList;
	// 지역 / 선호장르
	private List<String> region;     
	private List<String> genreList;
	private List<String> practiceDateList; 

}
