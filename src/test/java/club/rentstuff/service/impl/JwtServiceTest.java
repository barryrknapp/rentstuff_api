package club.rentstuff.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import club.rentstuff.entity.UserEntity;
import club.rentstuff.model.UserDto;
import club.rentstuff.service.JwtService;
import club.rentstuff.service.UserService;

@SpringBootTest
public class JwtServiceTest {
	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserService userService;

	@Test
	void testGenerateAndValidateToken() {
		UserDto user = userService.signUp(
				UserDto.builder().email("test@rentstuff.com").password("Password123").role("ROLE_USER").build());
		String token = jwtService.generateToken(
				UserEntity.builder().email(user.getEmail()).password(user.getPassword()).role(user.getRole()).build());
		assertTrue(jwtService.validateToken(token));
		assertEquals("test@rentstuff.com", jwtService.getEmailFromToken(token));
	}
}
