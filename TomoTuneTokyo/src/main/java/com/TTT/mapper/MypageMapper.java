package com.TTT.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.TTT.domain.BandHistoryDto;
import com.TTT.domain.MypageDto;

@Mapper
public interface MypageMapper {
    // 이력서 작성
    void insertResume(MypageDto mypageDto); 
    void insertBandHistory(BandHistoryDto bandHistoryDto);

    // 이력서 존재 여부 확인
    int countResumeByUserId(String userId);

    // 이력서 조회
    MypageDto selectResumeByUserId(String userId);  // ← 이 줄이 누락되어 있었음
}
