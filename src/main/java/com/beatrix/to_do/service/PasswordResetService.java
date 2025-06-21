package com.beatrix.to_do.service;

import com.beatrix.to_do.dto.auth.ForgotPasswordRequest;
import com.beatrix.to_do.dto.auth.PasswordResetRequest;
import com.beatrix.to_do.entity.TokenType;
import com.beatrix.to_do.entity.UserToken;
import com.beatrix.to_do.entity.User;
import com.beatrix.to_do.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserTokenService userTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new RuntimeException("User not found"));

        UserToken resetToken = userTokenService.createToken(
                user,
                TokenType.PASSWORD_RESET,
                2
        );
        emailService.sendPasswordResetEmail(user.getEmail(),resetToken.getToken());
    }

    public void resetPassword(PasswordResetRequest request) {
        UserToken resetToken = userTokenService.validateToken(
                request.getToken(),
                TokenType.PASSWORD_RESET
        );

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        userTokenService.markUsed(resetToken);
    }
}
