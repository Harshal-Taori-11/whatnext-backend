package com.whatnext.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Email service for sending emails
 * TODO: Configure SMTP settings in application.properties and implement actual email sending
 * For now, this logs the OTP to console for testing
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendOTPEmail(String toEmail, String otp) {
        // TODO: Implement actual email sending using JavaMailSender
        // For now, log the OTP for testing purposes
        logger.info("=".repeat(60));
        logger.info("📧 EMAIL SENT TO: {}", toEmail);
        logger.info("🔐 OTP CODE: {}", otp);
        logger.info("⏰ Valid for: 10 minutes");
        logger.info("=".repeat(60));

        // Simulating email content
        String emailContent = String.format("""
            Dear User,

            Your OTP for WhatNext! email verification is:

            %s

            This OTP is valid for 10 minutes.

            If you did not request this, please ignore this email.

            Best regards,
            WhatNext! Team
            """, otp);

        logger.info("Email Content:\n{}", emailContent);
    }

    public void sendWelcomeEmail(String toEmail, String fullName) {
        logger.info("=".repeat(60));
        logger.info("📧 WELCOME EMAIL SENT TO: {}", toEmail);
        logger.info("👤 User: {}", fullName);
        logger.info("=".repeat(60));

        String emailContent = String.format("""
            Dear %s,

            Welcome to WhatNext!

            Your account has been successfully created. You can now start organizing your tasks and notes.

            Get started:
            - Create your first task
            - Explore the board view
            - Try out the notes feature

            Happy organizing!

            Best regards,
            WhatNext! Team
            """, fullName);

        logger.info("Welcome Email Content:\n{}", emailContent);
    }
}
