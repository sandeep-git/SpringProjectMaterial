package com.appdeveloper.app.ws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.appdeveloper.app.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
	
	UserDto createUser(UserDto user);
	UserDto getUser(String email);
	UserDto getUserByUserId(String userId);
	UserDto updateUser(String userId,UserDto userdto);
	void deleteUser(String id);
	List<UserDto> getUsers(int page, int limit);

}
