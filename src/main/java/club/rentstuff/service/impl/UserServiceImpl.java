package club.rentstuff.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.UserEntity;
import club.rentstuff.model.UserDto;
import club.rentstuff.repo.UserRepo;
import club.rentstuff.service.ConfigService;
import club.rentstuff.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ConfigService configService;

	@Override
	public UserDto signUp(UserDto userDto) {
		// Validate input
		if (userDto.getEmail() == null || userDto.getPassword() == null) {
			throw new IllegalArgumentException("Email and password are required");
		}

		// Check if email already exists
		if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
			throw new IllegalStateException("Email already in use");
		}

		// Create user with hashed password
		UserEntity user = UserEntity.builder().email(userDto.getEmail())
				.password(passwordEncoder.encode(userDto.getPassword())).firstName(userDto.getFirstName())
				.lastName(userDto.getLastName()).role(userDto.getRole() != null ? userDto.getRole() : "ROLE_USER")
				.createDate(LocalDateTime.now()).build();

		if (!userDto.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
			throw new IllegalArgumentException("Password must contain letters and numbers");
		}

		if (userDto.getPassword().length() < Integer.parseInt(configService.getConfig("MIN_PASSWORD_LENGTH"))) {
			throw new IllegalArgumentException("Password too short");
		}

		UserEntity userEnt = userRepository.save(user);

		return UserDto.builder().email(userEnt.getEmail()).role(userEnt.getRole().replace("ROLE_", "")).build();
	}

	@Override
	public UserDto loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
		return UserDto.builder().email(user.getEmail()).password(user.getPassword())
				.role(user.getRole().replace("ROLE_", "")).build();
	}

}