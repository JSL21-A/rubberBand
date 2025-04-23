package com.TTT.domain;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class UserDto {
	
	private String user_id;
	private String username;
	private String password;
	private String email;
	private String role;
	private String status;
	private LocalDateTime created_at;
	private LocalDateTime updated_at;
	private LocalDateTime deleted_at;
	private String user_memo;

}
