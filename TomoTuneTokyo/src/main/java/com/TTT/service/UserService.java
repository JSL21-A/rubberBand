package com.TTT.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.TTT.domain.UserDto;
import com.TTT.mapper.UserMapper;

@Service
public class UserService {
	
	@Autowired
	UserMapper userMapper;
	@Autowired
	PasswordEncoder passwordEncoder;
	
	//ID(username) 중복체크
	public boolean usernameExists(String username) {
		return userMapper.checkUsernameExists(username) > 0;
	}
	
	public boolean nicknameExists(String nickname) {
		return userMapper.checkNicknameExists(nickname) > 0;
	}
	
	public void userInsert(UserDto userDto) {
		String user_id = "U" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
		
		userDto.setUser_id(user_id);
		userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
		
		userMapper.insertUser(userDto);
		userMapper.insertUserProfile(userDto);
	}
	
	
	
}
