package com.financiaplus.backend.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ApplicationRequest(
        String fullName,
        String address,
        LocalDate birthDate,
        String gender,
        String identityNumber,
        String email,
        String phone,
        BigDecimal biometricScore
) {
}