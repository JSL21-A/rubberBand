package com.TTT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TTT.mapper.UserMapper;

@Service
public class UserService {
	
	@Autowired
	UserMapper userMapper;
	
	//ID(username) 중복체크
	public boolean usernameExists(String username) {
		return userMapper.checkUsernameExists(username) > 0;
	}
	
	public boolean nicknameExists(String nickname) {
		return userMapper.checkNicknameExists(nickname) > 0;
	}
	
}
