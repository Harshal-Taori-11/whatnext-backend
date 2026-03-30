package com.whatnext.service;

import com.whatnext.exception.BadRequestException;
import com.whatnext.model.OTPVerification;
import com.whatnext.repository.OTPVerificationRepository;
import com.whatnext.util.CodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OTPService {

    private static final Logger logger = LoggerFactory.getLogger(OTPService.class);
    private static final int OTP_VALIDITY_MINUTES = 10;

    @Autowired
    private OTPVerificationRepository otpRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Generate and send OTP to email
     */
    @Transactional
    public void sendOTP(String email) {
        // Generate 6-digit OTP
        String otp = CodeGenerator.generateOTP();

        // Create OTP verification record
        OTPVerification otpVerification = new OTPVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtp(otp);
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
        otpVerification.setVerified(false);

        otpRepository.save(otpVerification);

        // Send email
        emailService.sendOTPEmail(email, otp);

        logger.info("OTP sent to email: {}", email);
    }

    /**
     * Verify OTP
     */
    @Transactional
    public boolean verifyOTP(String email, String otp) {
        Optional<OTPVerification> otpOpt = otpRepository.findByEmailAndOtpAndVerifiedFalse(email, otp);

        if (otpOpt.isEmpty()) {
            throw new BadRequestException("Invalid OTP");
        }

        OTPVerification otpVerification = otpOpt.get();

        if (otpVerification.isExpired()) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        // Mark as verified
        otpVerification.setVerified(true);
        otpRepository.save(otpVerification);

        logger.info("OTP verified successfully for email: {}", email);
        return true;
    }

    /**
     * Check if email has been verified
     */
    public boolean isEmailVerified(String email) {
        Optional<OTPVerification> otpOpt = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);
        return otpOpt.isPresent() && otpOpt.get().getVerified();
    }

    /**
     * Resend OTP
     */
    @Transactional
    public void resendOTP(String email) {
        // Check if recent OTP exists (within last minute to prevent spam)
        Optional<OTPVerification> recentOtp = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);

        if (recentOtp.isPresent()) {
            LocalDateTime lastSent = recentOtp.get().getCreatedAt();
            if (lastSent.plusMinutes(1).isAfter(LocalDateTime.now())) {
                throw new BadRequestException("Please wait before requesting another OTP");
            }
        }

        // Send new OTP
        sendOTP(email);
    }

    /**
     * Clean up expired OTPs (runs every hour)
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredOTPs() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        otpRepository.deleteByExpiresAtBefore(cutoffTime);
        logger.info("Cleaned up expired OTPs");
    }
}
