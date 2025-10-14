package club.rentstuff.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import club.rentstuff.model.UserDto;
import club.rentstuff.service.AuthService;


@SpringBootTest
public class AuthServiceTest {
	@Autowired
	private AuthService authService;

//	@Test
	public void testLogin() {
		UserDto userDto = UserDto.builder().email("test@rentstuff.com").password("Password123").build();
		String token = authService.login(userDto.getEmail(), userDto.getPassword());
		assertNotNull(token);
	}
}
