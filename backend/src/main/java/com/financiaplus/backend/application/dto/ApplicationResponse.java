package com.financiaplus.backend.application.dto;

import com.financiaplus.backend.application.Application;
import com.financiaplus.backend.application.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ApplicationResponse(
        UUID id,
        ApplicationStatus status,
        String fullName,
        String address,
        LocalDate birthDate,
        String gender,
        String identityNumber,
        String email,
        String phone,
        BigDecimal biometricScore,
        BigDecimal creditScore,
        String ipAddress,
        String country,
        String region,
        String city,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static ApplicationResponse from(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getStatus(),
                application.getFullName(),
                application.getAddress(),
                application.getBirthDate(),
                application.getGender(),
                application.getIdentityNumber(),
                application.getEmail(),
                application.getPhone(),
                application.getBiometricScore(),
                application.getCreditScore(),
                application.getIpAddress(),
                application.getCountry(),
                application.getRegion(),
                application.getCity(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}