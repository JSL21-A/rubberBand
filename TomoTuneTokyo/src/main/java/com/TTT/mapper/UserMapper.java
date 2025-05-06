package com.TTT.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.TTT.domain.UserDto;

@Mapper
public interface UserMapper {

	//ID(username) 중복체크
	@Select("select count(*) from users where username = #{username}")
	int checkUsernameExists(@Param("username") String username);
	
	//Nickname 중복체크
	@Select("select count(*) from user_profile where nickname = #{nickname}")
	int checkNicknameExists(@Param("nickname") String nickname);	
	
	//users에 회원가입 정보 insert
	@Insert("insert into users (user_id, username, password, email, role, status) values (#{user_id}, #{username}, #{password}, #{email}, 'U', 'A')")
	void insertUser(UserDto userDto);
	
	//user_profile에 회원가입 정보 insert
	@Insert("insert into user_profile (user_id, nickname) values (#{user_id}, #{nickname})")
	void insertUserProfile(UserDto userDto);
	
	//userLogin
	@Select("select user_id, username, password, role from users where username = #{username}")
	UserDto findByUsername(@Param("username") String username);
}