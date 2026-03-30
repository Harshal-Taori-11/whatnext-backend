package com.whatnext.repository;

import com.whatnext.model.OTPVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OTPVerificationRepository extends JpaRepository<OTPVerification, Long> {

    Optional<OTPVerification> findTopByEmailOrderByCreatedAtDesc(String email);

    Optional<OTPVerification> findByEmailAndOtpAndVerifiedFalse(String email, String otp);

    List<OTPVerification> findByExpiresAtBefore(LocalDateTime dateTime);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
