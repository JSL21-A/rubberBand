package com.TTT.service;

import org.springframework.stereotype.Service;
import com.TTT.domain.MypageDto;
import com.TTT.mapper.MypageMapper;

@Service
public class MypageService {

    private final MypageMapper mypageMapper; 

    public MypageService(MypageMapper mypageMapper) {
        this.mypageMapper = mypageMapper;
    }

    public void saveResume(MypageDto dto) {
        mypageMapper.insertResume(dto); 
    }
}//end
