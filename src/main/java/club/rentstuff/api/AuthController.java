package club.rentstuff.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import club.rentstuff.model.UserDto;
import club.rentstuff.service.AuthService;
import club.rentstuff.service.UserService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto) {
    	
        String token = authService.login(userDto.getEmail(), userDto.getPassword());
        return ResponseEntity.ok(token);
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> signUp(@RequestBody UserDto userDto) {
        UserDto user = userService.signUp(userDto);
        return login(userDto);
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Email from JWT
        UserDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }
}

