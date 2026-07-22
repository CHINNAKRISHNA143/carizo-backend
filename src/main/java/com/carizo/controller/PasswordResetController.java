package com.carizo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carizo.dto.ForgotPasswordRequest;
import com.carizo.dto.ResetPasswordRequest;
import com.carizo.service.PasswordResetService;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        passwordResetService.sendResetLink(request.getEmail());

        return ResponseEntity.ok(
                "If the email exists, a password reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(
                request.getToken(),
                request.getPassword());

        return ResponseEntity.ok("Password reset successfully.");
    }
}