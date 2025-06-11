package com.TTT.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.TTT.domain.UserDto;

public class CustomUserDetails implements UserDetails {
	
	private final UserDto userDto;
	
	public CustomUserDetails(UserDto userDto) {
		this.userDto = userDto;
	}
	
	public UserDto getUserDto() {
		return userDto;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return Collections.singleton(() -> "ROLE_" + userDto.getRole());
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return userDto.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return userDto.getUsername();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return "A".equalsIgnoreCase(userDto.getStatus());
	}

}
