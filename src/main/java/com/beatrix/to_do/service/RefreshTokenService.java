package com.beatrix.to_do.service;

import com.beatrix.to_do.dto.auth.SessionInfoResponse;
import com.beatrix.to_do.entity.RefreshToken;
import com.beatrix.to_do.entity.User;
import com.beatrix.to_do.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public RefreshToken createRefreshToken(User user, String deviceInfo, String ipAddress) {
        String refreshToken = jwtService.generateRefreshToken(user);
        RefreshToken entity = new RefreshToken();
        entity.setToken(refreshToken);
        entity.setUser(user);
        entity.setDeviceInfo(deviceInfo);
        entity.setIpAddress(ipAddress);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setExpiresAt(LocalDateTime.now().plusDays(7));
        entity.setRevoked(false);
        return refreshTokenRepository.save(entity);
    }

    public RefreshToken rotateRefreshToken(String oldToken){
        RefreshToken storedToken = refreshTokenRepository.findByToken(oldToken)
                .orElseThrow(()-> new RuntimeException("Invalid Refresh Token"));
        if(storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw  new RuntimeException("Refresh token is expired or revoked");
        }
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        return createRefreshToken(storedToken.getUser(),
                storedToken.getDeviceInfo(),
                storedToken.getIpAddress());
    }

    public List<SessionInfoResponse> getActiveSessions(User user) {
        return refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId())
                .stream()
                .map(token -> {
                    SessionInfoResponse response = new SessionInfoResponse();
                    response.setId(token.getId());
                    response.setDeviceInfo(token.getDeviceInfo());
                    response.setIpAddress(token.getIpAddress());
                    response.setCreatedAt(token.getCreatedAt());
                    response.setExpiresAt(token.getExpiresAt());
                    return response;
                })
                .toList();
    }

    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(()-> new RuntimeException("Invalid refresh token"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
