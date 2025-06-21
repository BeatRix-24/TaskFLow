package com.beatrix.to_do.repository;

import com.beatrix.to_do.entity.TokenType;
import com.beatrix.to_do.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Integer> {
    Optional<UserToken> findByTokenAndTokenTypeAndUsedFalse(String token, TokenType tokenType);
}
