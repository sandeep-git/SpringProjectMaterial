package com.appdeveloper.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.server.PathParam;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appdeveloper.app.ws.exceptions.UserServiceException;
import com.appdeveloper.app.ws.service.AddressService;
import com.appdeveloper.app.ws.service.UserService;
import com.appdeveloper.app.ws.shared.dto.AddressDTO;
import com.appdeveloper.app.ws.shared.dto.UserDto;
import com.appdeveloper.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appdeveloper.app.ws.ui.model.response.AddressesRest;
import com.appdeveloper.app.ws.ui.model.response.ErrorMessages;
import com.appdeveloper.app.ws.ui.model.response.OperationalStatusModel;
import com.appdeveloper.app.ws.ui.model.response.RequestOperationStatus;
import com.appdeveloper.app.ws.ui.model.response.UserRest;

import net.bytebuddy.description.type.TypeVariableToken;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressesService;

	@GetMapping(path="/{id}"/*,produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }*/)
	public UserRest getUser(@PathVariable String id) {

		UserRest returnValue = new UserRest();

		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);

		return returnValue;
	}

	@PostMapping(/*consumes= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }*/)
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

		UserRest returnValue = new UserRest();
		
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserRest.class);

		return returnValue;

	//	if(userDetails.getFirstName().isEmpty()) throw new NullPointerException("the object is null");

		/*UserDto userdto = new UserDto();
		BeanUtils.copyProperties(userDetails,userdto);*/
		
		/*ModelMapper modelMapper = new ModelMapper();
		UserDto userdto=modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userdto);
		System.out.println("printing create yser::"+createdUser);
		//BeanUtils.copyProperties(createdUser, returnValue);
		returnValue=modelMapper.map(createdUser, UserRest.class);
		
		System.out.println("printing return user value::"+returnValue);
		return returnValue;*/
	}

	@PutMapping(path="/{id}"/*consumes= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }*/)
	public UserRest updateUser(@PathVariable String id,@RequestBody UserDetailsRequestModel userDetails) {

		UserRest returnValue = new UserRest();

		if(userDetails.getFirstName().isEmpty()) throw new NullPointerException("the object is null");

		UserDto userdto = new UserDto();
		BeanUtils.copyProperties(userDetails,userdto);

		UserDto updatedUser = userService.updateUser(id,userdto);
		BeanUtils.copyProperties(updatedUser, returnValue);

		return returnValue;
	}

	@DeleteMapping(path="/{id}")
	public OperationalStatusModel deleteUser(@PathVariable String id) {
		
		OperationalStatusModel returnValue= new OperationalStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	@GetMapping(/*,produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }*/)
	public List<UserRest> getUsers(@RequestParam(value="page", defaultValue="1") int page, @RequestParam(value="limit", defaultValue="25") int limit) {

		List<UserRest> returnValue= new ArrayList<>();

		List<UserDto> users = userService.getUsers(page,limit);
		
		for(UserDto userDto:users) {
			UserRest userModel = new UserRest();	
		BeanUtils.copyProperties(userDto, userModel);
		returnValue.add(userModel);
		}

		return returnValue;
	}
	
	
	@GetMapping(path="/{id}/addresses"/*,produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }*/)
	public Resources<AddressesRest> getUserAddresses(@PathVariable String id) {

		List<AddressesRest> addressesListRestModel= new ArrayList<>();

		List<AddressDTO> addressesDto = addressesService.getAddresses(id);
		if(addressesDto!=null && !addressesDto.isEmpty()) {
			Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
			addressesListRestModel = new ModelMapper().map(addressesDto, listType);
					
					
			for (AddressesRest addressRest : addressesListRestModel) {
				Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId()))
						.withSelfRel();
				addressRest.add(addressLink);

				Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
				addressRest.add(userLink);
			}
		}

		return new Resources<>(addressesListRestModel);
	}
	
	@GetMapping(path="/{userId}/addresses/{addressId}"/*,produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }*/)
	public Resource<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

	//	AddressesRest returnValue= new AddressesRest();

		AddressDTO addressesDto = addressesService.getAddress(userId,addressId);
		ModelMapper mapper = new ModelMapper();
		
		Link addressLink = linkTo(UserController.class).slash(userId).slash("addresses").slash(addressId).withSelfRel();
		Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
		Link addressesLink = linkTo(UserController.class).slash(userId).slash("addresses").withRel("address");
		
		
		AddressesRest addressesRestModel= mapper.map(addressesDto, AddressesRest.class);
		
		addressesRestModel.add(addressLink);
		addressesRestModel.add(userLink);
		addressesRestModel.add(addressesLink);
		
		return new Resource<>(addressesRestModel);

	
	}
}

