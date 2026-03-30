package com.whatnext.controller;

import com.whatnext.dto.request.LoginRequest;
import com.whatnext.dto.request.OTPRequest;
import com.whatnext.dto.request.OTPVerifyRequest;
import com.whatnext.dto.request.RegisterRequest;
import com.whatnext.dto.response.AuthResponse;
import com.whatnext.dto.response.MessageResponse;
import com.whatnext.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Step 1: Send OTP to email
     */
    @PostMapping("/send-otp")
    public ResponseEntity<MessageResponse> sendOTP(@Valid @RequestBody OTPRequest request) {
        authService.sendRegistrationOTP(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("OTP sent to " + request.getEmail()));
    }

    /**
     * Step 2: Verify OTP
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<MessageResponse> verifyOTP(@Valid @RequestBody OTPVerifyRequest request) {
        boolean verified = authService.verifyRegistrationOTP(request.getEmail(), request.getOtp());
        if (verified) {
            return ResponseEntity.ok(new MessageResponse("Email verified successfully"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired OTP"));
        }
    }

    /**
     * Resend OTP
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<MessageResponse> resendOTP(@Valid @RequestBody OTPRequest request) {
        authService.sendRegistrationOTP(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("OTP resent to " + request.getEmail()));
    }

    /**
     * Step 3: Complete registration
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
