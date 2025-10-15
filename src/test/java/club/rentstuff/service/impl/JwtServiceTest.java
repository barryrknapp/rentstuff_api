package club.rentstuff.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import club.rentstuff.entity.UserEntity;
import club.rentstuff.model.UserDto;
import club.rentstuff.service.JwtService;
import club.rentstuff.service.UserService;
import io.jsonwebtoken.Jwts;

//@SpringBootTest
public class JwtServiceTest {
	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserService userService;

	//if you are setting up a new database, you can use this to generate a new JWT key
	@Test
	void testGenerateNewSecret() {
		

		SecretKey key = Jwts.SIG.HS512.key().build(); 
		String base64 = Base64.getEncoder().encodeToString(key.getEncoded());
		System.out.println("Calculate JWT Key:" + base64);
		assertTrue(base64.length()>0);
		
	}
//	@Test
	void testGenerateAndValidateToken() {
		UserDto user = userService.signUp(
				UserDto.builder().email("test@rentstuff.com").password("Password123").role("ROLE_USER").build());
		String token = jwtService.generateToken(
				UserEntity.builder().email(user.getEmail()).password(user.getPassword()).role(user.getRole()).build());
		assertTrue(jwtService.validateToken(token));
		assertEquals("test@rentstuff.com", jwtService.getEmailFromToken(token));
	}
}
