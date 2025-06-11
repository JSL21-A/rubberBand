package com.TTT.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.TTT.domain.UserDto;
import com.TTT.mapper.UserMapper;
import com.TTT.security.CustomUserDetails;

@Service
public class UserDetailService implements UserDetailsService {
	private final UserMapper mapper;

	public UserDetailService(UserMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		UserDto userDto = mapper.findByUsername(username);
		if (userDto == null) {
			throw new UsernameNotFoundException(username);
		}
		return new CustomUserDetails(userDto);
	}

}
