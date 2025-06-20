package com.beatrix.to_do.controller;


import com.beatrix.to_do.dto.auth.AuthenticationRequest;
import com.beatrix.to_do.dto.auth.AuthenticationResponse;
import com.beatrix.to_do.dto.auth.RegisterRequest;
import com.beatrix.to_do.service.AuthenticationService;
import com.beatrix.to_do.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthenticationResponse> signUp(
            @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
            ){
        return ResponseEntity.ok(service.register(request,httpRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request,
            HttpServletRequest httpRequest
    ){
        return ResponseEntity.ok(service.authenticate(request,httpRequest));
    }
}
