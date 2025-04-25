package com.TTT.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

	//ID(username) 중복체크
	@Select("select count(*) from users where username = #{username}")
	int checkUsernameExists(@Param("username") String username);
	
	//Nickname 중복체크
	@Select("select count(*) from user_profile where nickname = #{nickname}")
	int checkNicknameExists(@Param("nickname") String nickname);	
	
	//users에 회원가입 정보 insert
	
	//user_profile에 회원가입 정보 insert
}