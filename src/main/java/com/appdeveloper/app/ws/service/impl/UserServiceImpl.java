package com.appdeveloper.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appdeveloper.app.ws.exceptions.UserServiceException;
import com.appdeveloper.app.ws.io.entity.UserEntity;
import com.appdeveloper.app.ws.io.repositories.UserRepository;
import com.appdeveloper.app.ws.service.UserService;
import com.appdeveloper.app.ws.shared.dto.AddressDTO;
import com.appdeveloper.app.ws.shared.dto.UserDto;
import com.appdeveloper.app.ws.shared.util.Utils;
import com.appdeveloper.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {

		UserEntity storedUserDetail = userRepository.findByEmail(user.getEmail());

		if(storedUserDetail!=null) throw new RuntimeException("Record Already Exists");
		
		System.out.println("size of address::"+user.getAddresses().size());
		System.out.println("size of address::");
		for(int i=0;i<user.getAddresses().size();i++)
		{
			AddressDTO address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(10));
			user.getAddresses().set(i, address);
		}


		/*UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user,userEntity);x*/
		
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);

		String userId = utils.generateUserId(30);
		System.out.println("User id value::"+userId);
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));


		UserEntity storedUserDetails = userRepository.save(userEntity);

		/*UserDto returnValue = new UserDto();

		BeanUtils.copyProperties(storedUserDetails,returnValue);*/
		
		UserDto returnValue= modelMapper.map(storedUserDetails, UserDto.class);

		return returnValue;
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);

		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserEntity userEntity=userRepository.findByEmail(email);

		if(userEntity==null) throw new UsernameNotFoundException(email);

		return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),new ArrayList<>());
	}

	@Override
	public UserDto getUserByUserId(String userId) {

		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()+userId);

		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);

		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto userdto) {

		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userEntity.setFirstName(userdto.getFirstName());
		userEntity.setLastName(userdto.getLastName());


		UserEntity updatedUserDetails = userRepository.save(userEntity);

		BeanUtils.copyProperties(updatedUserDetails,returnValue);

		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {
		
		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userRepository.delete(userEntity);
		
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
List<UserDto> returnValue = new ArrayList<>();
		
		if(page>0) page = page-1;
		
		Pageable pageableRequest = PageRequest.of(page, limit);
		
		Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = usersPage.getContent();
		
        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }
		
		return returnValue;
	}

}
