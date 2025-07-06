package com.beatrix.to_do.service;

import com.beatrix.to_do.dto.auth.AuthenticationRequest;
import com.beatrix.to_do.dto.auth.AuthenticationResponse;
import com.beatrix.to_do.dto.auth.RegisterRequest;
import com.beatrix.to_do.dto.auth.VerifyEmailRequest;
import com.beatrix.to_do.entity.*;
import com.beatrix.to_do.exception.EmailNotVerifiedException;
import com.beatrix.to_do.exception.InvalidOtpException;
import com.beatrix.to_do.exception.UserNotFoundException;
import com.beatrix.to_do.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final OtpRedisService redisService;

    private User saveUser(RegisterRequest request){
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isVerified(false)
                .build();
        return userRepository.save(user);
    }

    public void register(RegisterRequest request) {
        User user = saveUser(request);

        String generatedOtp = redisService.generateOtp();
        redisService.saveOtp("VERIFY", user.getEmail(), generatedOtp);
        emailService.sendVerificationEmail(user.getEmail(), generatedOtp);
    }


    public AuthenticationResponse verifyEmail(VerifyEmailRequest request, HttpServletRequest httpRequest){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UserNotFoundException("User not found with email : " + request.getEmail()));

        String storedOtp = redisService.getOtp("VERIFY", user.getEmail());
        if(storedOtp == null || !storedOtp.equals(request.getOtpCode())){
            throw new InvalidOtpException("Invalid or expired OTP");
        }
        user.setVerified(true);
        userRepository.save(user);

        return issueToken(user,httpRequest);
    }

    private AuthenticationResponse issueToken(User user, HttpServletRequest httpRequest) {
        String accessToken = jwtService.generateToken(user);
        String deviceInfo = httpRequest.getHeader("user-agent");
        String ipAddress = httpRequest.getRemoteAddr();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user,deviceInfo,ipAddress);

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
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UserNotFoundException("User not found with email : " + request.getEmail()));
        if(!user.isVerified()){
            throw new EmailNotVerifiedException(user.getEmail() + " this email is not verified");
        }

        return issueToken(user,httpRequest);
    }
}
