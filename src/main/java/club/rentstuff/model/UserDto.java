package club.rentstuff.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;
}