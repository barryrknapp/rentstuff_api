package club.rentstuff.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.UserEntity;
import club.rentstuff.repo.UserRepo;
import club.rentstuff.service.AuthService;
import club.rentstuff.service.JwtService;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @Autowired
    private JwtService jwtService;
    

    @Override
    public String login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return jwtService.generateToken(user);
    }
    
    @Override
    public Optional<UserEntity> getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String email = authentication.getName();
	    return userRepository.findByEmail(email);
	    
    	
    }
    
    
}