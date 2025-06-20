package com.beatrix.to_do.service;

import com.beatrix.to_do.dto.auth.AuthenticationRequest;
import com.beatrix.to_do.dto.auth.AuthenticationResponse;
import com.beatrix.to_do.dto.auth.RegisterRequest;
import com.beatrix.to_do.entity.RefreshToken;
import com.beatrix.to_do.entity.Role;
import com.beatrix.to_do.entity.User;
import com.beatrix.to_do.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    private User saveUser(RegisterRequest request){
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        return repository.save(user);
    }

    public AuthenticationResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        User user = saveUser(request);
        String accessToken = jwtService.generateToken(user);
        String deviceInfo = httpRequest.getHeader("user-agent");
        String ipAddress = httpRequest.getRemoteAddr();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user,deviceInfo, ipAddress);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        String accessToken = jwtService.generateToken(user);
        String deviceInfo = httpRequest.getHeader("user-agent");
        String ipAddress = httpRequest.getRemoteAddr();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user,deviceInfo,ipAddress);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
