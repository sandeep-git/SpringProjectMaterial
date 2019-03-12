package com.appdeveloper.app.ws.shared.util;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class Utils {
	 
	private final Random RANDOM =new SecureRandom();
	private final String ALPHABET ="0123456789ABCDEFGHIJKLMNOPQRSTUUVWXYZabcdefghijklmnopqrstuvwxyz";
	

	public String generateUserId(int length) {
		return getRandomString(length);
	}


	public String generateAddressId(int length) {
		return getRandomString(length);
	}
	
	private String getRandomString(int length) {
		StringBuilder returnValue = new StringBuilder(length);
		
		for(int i=0;i<length;i++) {
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		
		return returnValue.toString();
	}
}
