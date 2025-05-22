package com.TTT.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.TTT.domain.BandHistoryDto;
import com.TTT.domain.MypageDto;

@Mapper
public interface MypageMapper {
    void insertResume(MypageDto mypageDto); 
    void insertBandHistory(BandHistoryDto bandHistoryDto);
}

