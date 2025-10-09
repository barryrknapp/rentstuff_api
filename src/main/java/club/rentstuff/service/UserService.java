package club.rentstuff.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import club.rentstuff.model.UserDto;

@Service
public interface UserService {

	UserDto signUp(UserDto userDto);

	UserDto loadUserByUsername(String email) throws UsernameNotFoundException;
}