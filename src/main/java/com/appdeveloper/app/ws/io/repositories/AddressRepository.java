package com.appdeveloper.app.ws.io.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.appdeveloper.app.ws.io.entity.AddressEntity;
import com.appdeveloper.app.ws.io.entity.UserEntity;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
	
	List<AddressEntity> findAllByUserDetails(UserEntity entity);
	AddressEntity findByAddressId(String addressId);

}
