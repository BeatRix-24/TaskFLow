package com.beatrix.taskflow.service;

import com.beatrix.taskflow.dto.auth.ForgotPasswordRequest;
import com.beatrix.taskflow.dto.auth.PasswordResetRequest;
import com.beatrix.taskflow.entity.User;
import com.beatrix.taskflow.exception.InvalidOtpException;
import com.beatrix.taskflow.exception.UserNotFoundException;
import com.beatrix.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final OtpRedisService redisService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;



    public void sendOtpForPasswordReset(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UserNotFoundException("User not found with email : " + request.getEmail()));

        String generatedOtp = redisService.generateOtp();
        redisService.saveOtp("RESET", user.getEmail(), generatedOtp);

        emailService.sendPasswordResetEmail(user.getEmail(), generatedOtp);
    }

    public void resetPassword(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UserNotFoundException("User not found with email : " + request.getEmail()));

        String storedOtp = redisService.getOtp("RESET", user.getEmail());

        if(storedOtp == null || !storedOtp.equals(request.getToken())){
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        redisService.invalidateOtp("RESET", user.getEmail());
    }
}
