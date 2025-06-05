package com.TTT.domain;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.Data;


public class UserDto {
	//users
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
	//user_profile
	private String nickname;
	private String user_img;
	
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getCreated_at() {
		return created_at;
	}
	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
	public LocalDateTime getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(LocalDateTime updated_at) {
		this.updated_at = updated_at;
	}
	public LocalDateTime getDeleted_at() {
		return deleted_at;
	}
	public void setDeleted_at(LocalDateTime deleted_at) {
		this.deleted_at = deleted_at;
	}
	public String getUser_memo() {
		return user_memo;
	}
	public void setUser_memo(String user_memo) {
		this.user_memo = user_memo;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getUser_img() {
		return user_img;
	}
	public void setUser_img(String user_img) {
		this.user_img = user_img;
	}
	
	

}
