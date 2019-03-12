package com.appdeveloper.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appdeveloper.app.ws.io.entity.AddressEntity;
import com.appdeveloper.app.ws.io.entity.UserEntity;
import com.appdeveloper.app.ws.io.repositories.AddressRepository;
import com.appdeveloper.app.ws.io.repositories.UserRepository;
import com.appdeveloper.app.ws.service.AddressService;
import com.appdeveloper.app.ws.shared.dto.AddressDTO;

@Service
public class AddressServiceImpl implements AddressService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;

	@Override
	public List<AddressDTO> getAddresses(String userId) {

		List<AddressDTO> returnValue = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		
		UserEntity entity = userRepository.findByUserId(userId);
		if(entity==null) return returnValue;
		
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(entity);
		for(AddressEntity addressList: addresses) {
			returnValue.add(modelMapper.map(addressList, AddressDTO.class));
		}
		return returnValue;
	}

	@Override
	public AddressDTO getAddress(String userId,String addressId) {

		AddressDTO returnValue=null;
		AddressEntity addressEntity =addressRepository.findByAddressId(addressId);
		if(addressEntity!=null) {
			returnValue= new ModelMapper().map(addressEntity, AddressDTO.class);
		}
		return returnValue;
	}

}
