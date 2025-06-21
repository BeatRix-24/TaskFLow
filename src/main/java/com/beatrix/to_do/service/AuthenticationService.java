package com.beatrix.to_do.service;

import com.beatrix.to_do.dto.auth.AuthenticationRequest;
import com.beatrix.to_do.dto.auth.AuthenticationResponse;
import com.beatrix.to_do.dto.auth.RegisterRequest;
import com.beatrix.to_do.dto.auth.VerifyEmailRequest;
import com.beatrix.to_do.entity.RefreshToken;
import com.beatrix.to_do.entity.Role;
import com.beatrix.to_do.entity.User;
import com.beatrix.to_do.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;

    private User saveUser(RegisterRequest request){
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isVerified(false)
                .otpCode(generateOtp())
                .otpExpiration(LocalDateTime.now().plusMinutes(1))
                .build();
        return repository.save(user);
    }

    private String generateOtp() {
        return String.format("%06d",new Random().nextInt(1000000));
    }

    public void register(RegisterRequest request) {
        User user = saveUser(request);
        sendVerificationEmail(user.getEmail(), user.getOtpCode());
    }

    private void sendVerificationEmail(String email, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify your email");
        message.setText("Your verfication code is: " + otpCode);
        mailSender.send(message);
    }

    public AuthenticationResponse verifyEmail(VerifyEmailRequest request, HttpServletRequest httpRequest){
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(()-> new RuntimeException("User not found"));

        if(!user.getOtpCode().equals(request.getOtpCode()) || user.getOtpExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        user.setVerified(true);
        user.setOtpCode(null);
        user.setOtpExpiration(null);
        repository.save(user);


        String accessToken = jwtService.generateToken(user);
        String deviceInfo = httpRequest.getHeader("user-agent");
        String ipAddress = httpRequest.getRemoteAddr();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, deviceInfo,ipAddress);

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
        if(!user.isVerified()){
            throw new RuntimeException("Email not verified");
        }

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
