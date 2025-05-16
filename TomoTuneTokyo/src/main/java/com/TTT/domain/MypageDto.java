package com.TTT.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data

public class MypageDto {

	// 이력서 항목
	private int resumeId; // 이력서 ID
	private String userId; // 유저 아이디
	private String resumeName; // 이력서명 (사용X)
	private LocalDateTime createdAt; // 등록일시 (사용X)
	private LocalDateTime updatedAt; // 수정일시 (사용X)
	// 기본정보
	private String stageName; // 활동명
	private String gender; // 성별
	private String birth; // 생년월일
	private String sns; // 이메일
	private String phoneNumber; // 연락처
	// 합주정보
	private String area; // 활동지역
	private String instrument; // 밴드포지션
	private String genre; // 선호장르
	private String practiceDate; // 연습가능요일
	private String practiceTime; // 연습가능시간대
	private String detailTime; // 구체적인 시간
	// 밴드활동정보
	//ID 필요할까? (3개까지 등록가능하니까)
	private String bandCareer; // 밴드활동경험유무
	private String bandName; // 활동밴드이름
	// 포지션 필요 !! 
	private String bandPeriod; // 밴드활동기간
	// 자유입력
	private String photo; // 프로필사진(사용X)
	private String note; // 자기소개

}
