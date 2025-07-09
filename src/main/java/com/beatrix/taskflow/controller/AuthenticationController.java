package com.beatrix.taskflow.controller;


import com.beatrix.taskflow.dto.auth.AuthenticationRequest;
import com.beatrix.taskflow.dto.auth.AuthenticationResponse;
import com.beatrix.taskflow.dto.auth.RegisterRequest;
import com.beatrix.taskflow.dto.auth.VerifyEmailRequest;
import com.beatrix.taskflow.service.AuthenticationService;
import com.beatrix.taskflow.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(
            @RequestBody RegisterRequest request
            ){
        authService.register(request);
        return ResponseEntity.ok("User registered successfully. Check your email for the verification code");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request,
            HttpServletRequest httpRequest
    ){
        return ResponseEntity.ok(authService.authenticate(request,httpRequest));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<AuthenticationResponse> verifyEmail(@RequestBody VerifyEmailRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.verifyEmail(request,httpRequest));
    }
}
