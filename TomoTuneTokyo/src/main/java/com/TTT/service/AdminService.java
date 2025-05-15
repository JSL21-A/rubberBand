package com.TTT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TTT.domain.UserDto;
import com.TTT.mapper.AdminMapper;

@Service
public class AdminService {
    @Autowired
    AdminMapper adminMapper;

    public void modalTest(UserDto userDto) {
        userDto.setNickname(adminMapper.modalTest().getNickname());
    }
}
