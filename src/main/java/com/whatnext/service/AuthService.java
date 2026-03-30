package com.whatnext.service;

import com.whatnext.dto.request.LoginRequest;
import com.whatnext.dto.request.RegisterRequest;
import com.whatnext.dto.response.AuthResponse;
import com.whatnext.exception.BadRequestException;
import com.whatnext.model.User;
import com.whatnext.model.UserSettings;
import com.whatnext.repository.UserRepository;
import com.whatnext.repository.UserSettingsRepository;
import com.whatnext.security.JwtTokenProvider;
import com.whatnext.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    /**
     * Step 1: Send OTP to email
     */
    public void sendRegistrationOTP(String email) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered");
        }

        otpService.sendOTP(email);
    }

    /**
     * Step 2: Verify OTP
     */
    public boolean verifyRegistrationOTP(String email, String otp) {
        return otpService.verifyOTP(email, otp);
    }

    /**
     * Step 3: Complete registration with profile details
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Verify OTP was completed
        if (!otpService.isEmailVerified(request.getEmail())) {
            throw new BadRequestException("Email must be verified first");
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already taken");
        }

        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        // Generate unique task code
        String taskCode = generateUniqueTaskCode(request.getUsername());

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCountryCode(request.getCountryCode());
        user.setProfilePictureUrl(request.getProfilePictureUrl());
        user.setEmailVerified(true);
        user.setTaskCode(taskCode);

        User savedUser = userRepository.save(user);

        // Create default settings for user
        UserSettings settings = new UserSettings();
        settings.setUser(savedUser);
        userSettingsRepository.save(settings);

        // Send welcome email
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFullName());

        // Authenticate and return token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return new AuthResponse(jwt, savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getFullName());
    }

    /**
     * Generate unique task code from username
     */
    private String generateUniqueTaskCode(String username) {
        String taskCode = CodeGenerator.generateTaskCode(username);

        // Ensure uniqueness
        int attempts = 0;
        while (userRepository.existsByTaskCode(taskCode) && attempts < 10) {
            taskCode = CodeGenerator.generateTaskCode(username + attempts);
            attempts++;
        }

        if (attempts >= 10) {
            throw new BadRequestException("Unable to generate unique task code");
        }

        return taskCode;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        return new AuthResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), user.getFullName());
    }
}
