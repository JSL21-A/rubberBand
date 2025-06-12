package com.TTT.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
	@Select("select u.user_id, u.username, u.password, u.email, u.role, u.status, u.created_at, u.updated_at, deleted_at, u.user_memo, p.nickname, p.user_img\n"
			+ "from users u\n"
			+ "left join user_profile p on u.user_id = p.user_id where username = #{username}")
	UserDto findByUsername(@Param("username") String username);
	
	//아이디, 이메일 일치여부 확인
	@Select("select count(*) from users where username=#{username} and email=#{email}")
	int checkUsernameEmail(@Param("username") String username, @Param("email") String email);
	
	//비밀번호 초기화
	@Update("update users set password = #{password} where username=#{username}")
	void updatePassword(@Param("username") String username, @Param("password") String password);
	
	//userId로 User 정보 조회
	@Select("select user_id, password, email from users where user_id = #{userId}")
	UserDto selectByUserId(@Param("userId") String userId);
	
	//user테이블 이메일 변경
	@Update("update users set email = #{newEmail} where user_id = #{userId}")
	int updateEmailByUserId(@Param("userId") String userId, @Param("newEmail") String newEmail);
	
	
	
	
}