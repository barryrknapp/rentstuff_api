package club.rentstuff.service;

import club.rentstuff.entity.UserEntity;

public interface JwtService {
    String generateToken(UserEntity user);
    String getEmailFromToken(String token);
    boolean validateToken(String token);
}