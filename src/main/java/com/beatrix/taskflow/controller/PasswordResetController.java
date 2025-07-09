package com.beatrix.taskflow.controller;

import com.beatrix.taskflow.dto.auth.ForgotPasswordRequest;
import com.beatrix.taskflow.dto.auth.PasswordResetRequest;
import com.beatrix.taskflow.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class PasswordResetController {
   private final PasswordResetService passwordResetService;

   @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request){
      passwordResetService.sendOtpForPasswordReset(request);
      return ResponseEntity.ok("Reset email sent successfully");
   }

   @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request){
       passwordResetService.resetPassword(request);
       return ResponseEntity.ok("Password reset successful");
   }
}
