package com.appdeveloper.app.ws.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;

import com.appdeveloper.app.ws.io.entity.AddressEntity;
import com.appdeveloper.app.ws.io.entity.UserEntity;
import com.appdeveloper.app.ws.io.repositories.UserRepository;
import com.appdeveloper.app.ws.shared.dto.AddressDTO;
import com.appdeveloper.app.ws.shared.dto.UserDto;
import com.appdeveloper.app.ws.shared.util.Utils;

class UserServiceImplTest {
	
	@InjectMocks
	UserServiceImpl userServiceImpl;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	Utils utils;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	UserEntity userEntity;
	
	final String userId="qwerersd";

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Sandeep");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword("223238bnsd");
		userEntity.setAddresses(getAddressesEntity());
	}

	@Test
	final void testGetUser() {
		
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto= userServiceImpl.getUser("test@test.com");
		
		assertNotNull(userDto);
		
		assertEquals("Sandeep", userDto.getFirstName());
		
	}
	
	@Test
	final void testGetUser_UsernameNotFoundException() {
	
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		assertThrows(UsernameNotFoundException.class,
				()->{
					userServiceImpl.getUser("test@test.com");
				});
	}
	
	@Test
	final void testCreateUser() {
		
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("12942neie");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("223238bnsd");
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
	/*	
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setType("communication");
		
		List<AddressDTO> listAddr = new ArrayList<AddressDTO>();
		listAddr.add(addressDTO);*/
		
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Sandeep");
		userDto.setLastName("Gupta");
		userDto.setPassword("223238bnsd");
		userDto.setEmail("test@test.com");

		UserDto storedUserDetails=userServiceImpl.createUser(userDto);
		assertNotNull(storedUserDetails);
		
		assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
		assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
		assertNotNull(userEntity.getUserId());
		assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
		verify(utils,times(storedUserDetails.getAddresses().size())).generateAddressId(10);
		verify(bCryptPasswordEncoder, times(1)).encode("223238bnsd");
		verify(userRepository,times(1)).save(any(UserEntity.class));
	}
	
	private List<AddressDTO> getAddressesDto() {
		AddressDTO addressDto = new AddressDTO();
		addressDto.setType("shipping");
		addressDto.setCity("Vancouver");
		addressDto.setCountry("Canada");
		addressDto.setPostalCode("ABC123");
		addressDto.setStreetName("123 Street name");

		AddressDTO billingAddressDto = new AddressDTO();
		billingAddressDto.setType("billling");
		billingAddressDto.setCity("Vancouver");
		billingAddressDto.setCountry("Canada");
		billingAddressDto.setPostalCode("ABC123");
		billingAddressDto.setStreetName("123 Street name");

		List<AddressDTO> addresses = new ArrayList<>();
		addresses.add(addressDto);
		addresses.add(billingAddressDto);

		return addresses;

	}
	
	private List<AddressEntity> getAddressesEntity()
	{
		List<AddressDTO> addresses = getAddressesDto();
		
	    Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
	    
	    return new ModelMapper().map(addresses, listType);
	}

}
