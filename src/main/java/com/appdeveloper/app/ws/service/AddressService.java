package com.appdeveloper.app.ws.service;

import java.util.List;

import com.appdeveloper.app.ws.shared.dto.AddressDTO;

public interface AddressService {
	
	List<AddressDTO> getAddresses(String userId);
	
	AddressDTO getAddress(String userId,String addressId);

}
