package com.TTT.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TTT.domain.PostVo;
import com.TTT.domain.UserDto;
import com.TTT.mapper.AdminMapper;
import com.TTT.mapper.PublicMapper;

@Service
public class AdminService {

    @Autowired
    AdminMapper adminMapper;

    public void modalTest(UserDto userDto) {
        userDto.setNickname(adminMapper.modalTest().getNickname());
    }

    public List<PostVo> getNotiList() {
        return adminMapper.getNotiList();
    }
}
