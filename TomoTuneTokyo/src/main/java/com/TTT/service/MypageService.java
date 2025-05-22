package com.TTT.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.TTT.domain.BandHistoryDto;
import com.TTT.domain.MypageDto;
import com.TTT.mapper.MypageMapper;

@Service
public class MypageService {

    @Autowired
    private MypageMapper mypageMapper;

    // 이력서 작성 (중복 작성 방지 포함)
    public void saveResume(MypageDto dto) {
        String userId = dto.getUserId();
        if (hasResume(userId)) {
            throw new IllegalStateException("このユーザーはすでに履歴書を作成しています。");
        }

        // 활동가능지역
        if (dto.getRegion() != null && !dto.getRegion().isEmpty()) {
            dto.setArea(String.join(",", dto.getRegion()));
        }

        // 선호음악장르
        if (dto.getGenreList() != null && !dto.getGenreList().isEmpty()) {
            dto.setGenre(String.join(",", dto.getGenreList()));
        }

        // 연습가능요일
        if (dto.getPracticeDateList() != null && !dto.getPracticeDateList().isEmpty()) {
            String converted = dto.getPracticeDateList().stream().map(this::convertDayToChar)
                    .collect(Collectors.joining(","));
            dto.setPracticeDate(converted);
        }

        // 연습시간대
        dto.setPracticeTime(convertPracticeTime(dto.getPracticeTime()));

        // 이력서 insert
        mypageMapper.insertResume(dto);
        int resumeId = (int) dto.getResumeId();

        // 밴드활동이력 추가
        List<BandHistoryDto> bandList = dto.getBandHistoryList();
        if (bandList != null) {
            for (BandHistoryDto bh : bandList) {
                String bandName = bh.getBandName();
                if (bandName == null || bandName.trim().isEmpty()) continue;

                bh.setResumeId(resumeId);
                mypageMapper.insertBandHistory(bh);
            }
        }
    }

    // 연습시간대 변환
    private String convertPracticeTime(String time) {
        if (time == null) return null;
        switch (time) {
            case "morning": return "MORNING";
            case "afternoon": return "AFTERNOON";
            case "evening": return "EVENING";
            case "other": return "OTHER";
            default: return "_";
        }
    }

    // 요일 변환
    private String convertDayToChar(String day) {
        switch (day) {
            case "月": return "MON";
            case "火": return "TUE";
            case "水": return "WED";
            case "木": return "THU";
            case "金": return "FRI";
            case "土": return "SAT";
            case "日": return "SUN";
            default: return "_";
        }
    }

    // 이력서 존재 여부
    public boolean hasResume(String userId) {
        return mypageMapper.countResumeByUserId(userId) > 0;
    }

    // 이력서 조회
    public MypageDto getResumeByUserId(String userId) {
        return mypageMapper.selectResumeByUserId(userId);
    }
}
