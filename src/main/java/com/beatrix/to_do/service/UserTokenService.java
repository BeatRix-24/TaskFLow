package com.beatrix.to_do.service;

import com.beatrix.to_do.entity.TokenType;
import com.beatrix.to_do.entity.User;
import com.beatrix.to_do.entity.UserToken;
import com.beatrix.to_do.exception.InvalidTokenException;
import com.beatrix.to_do.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
@Service
@RequiredArgsConstructor
public class UserTokenService {
    private final UserTokenRepository tokenRepository;

    public UserToken createToken(User user, TokenType tokenType, int expiresInMinutes) {
        UserToken token = new UserToken();
        token.setUser(user);
        token.setToken(String.format("06%d", new Random().nextInt(1000000)));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(expiresInMinutes));
        token.setTokenType(tokenType);
        return tokenRepository.save(token);
    }

    public UserToken validateToken(String token, TokenType tokenType) {
        UserToken userToken = tokenRepository.findByTokenAndTokenTypeAndUsedFalse(token , tokenType)
                .orElseThrow(() -> new InvalidTokenException("Invalid Token"));

        if(userToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new InvalidTokenException("Token expired");
        }
        return userToken;
    }

    public void markUsed(UserToken userToken){
       userToken.setUsed(true);
       tokenRepository.save(userToken);
    }
}
