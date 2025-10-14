package club.rentstuff.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import club.rentstuff.entity.UserEntity;
import club.rentstuff.model.UserDto;
import club.rentstuff.service.UserService;

@SpringBootTest
public class UserServiceTest {
	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;

//	@Test
	public void testSignUp() {
		UserDto userDto = UserDto.builder().email("test@rentstuff.com").password("Password123").build();
		UserDto user = userService.signUp(userDto);
		assertTrue(passwordEncoder.matches("Password123", user.getPassword()));
	}
}
