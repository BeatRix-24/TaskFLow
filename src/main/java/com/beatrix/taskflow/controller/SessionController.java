package com.beatrix.taskflow.controller;

import com.beatrix.taskflow.dto.auth.RefreshTokenRequest;
import com.beatrix.taskflow.dto.auth.RefreshTokenResponse;
import com.beatrix.taskflow.dto.auth.SessionInfoResponse;
import com.beatrix.taskflow.entity.RefreshToken;
import com.beatrix.taskflow.entity.User;
import com.beatrix.taskflow.exception.UserNotFoundException;
import com.beatrix.taskflow.repository.UserRepository;
import com.beatrix.taskflow.service.JwtService;
import com.beatrix.taskflow.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class SessionController {

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(()-> new UserNotFoundException("User not found with email" + auth.getName()));
    }
    @GetMapping("/sessions")
    public List<SessionInfoResponse> getSessions() {
        return refreshTokenService.getActiveSessions(getCurrentUser());
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request,
                                                        HttpServletRequest httpRequest) {
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(request.getRefreshToken());

        String accessToken = jwtService.generateToken(newRefreshToken.getUser());

        return ResponseEntity.ok(new RefreshTokenResponse(accessToken, newRefreshToken.getToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request){
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
