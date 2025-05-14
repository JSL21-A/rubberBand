package com.TTT.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
	@Autowired
	private SendbirdClient sendbirdClient;
	
	//ID(username) 중복체크
	public boolean usernameExists(String username) {
		return userMapper.checkUsernameExists(username) > 0;
	}
	//닉네임(nickname) 중복체크
	public boolean nicknameExists(String nickname) {
		return userMapper.checkNicknameExists(nickname) > 0;
	}
	//회원가입 + USER_ID 생성
	public void userInsert(UserDto userDto) {
		String user_id = "U" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
		
		userDto.setUser_id(user_id);
		userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
		
		userMapper.insertUser(userDto);
		userMapper.insertUserProfile(userDto);
		
		try {
			sendbirdClient.createUser(user_id, userDto.getNickname());
		}catch(Exception e) {
			throw new IllegalStateException("Sendbird 유저 생성 실패", e);
		}
		
	}
	//비밀번호 초기화
	public void updatePassword(String username, String rawPassword) {
		String password = passwordEncoder.encode(rawPassword);
		
		userMapper.updatePassword(username, password);
	}
	
	public boolean isUsernameEmailMatch(String username, String email) {
		return userMapper.checkUsernameEmail(username, email) > 0;
	}
	
	
}
