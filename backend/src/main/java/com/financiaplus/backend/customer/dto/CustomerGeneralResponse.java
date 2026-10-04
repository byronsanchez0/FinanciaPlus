package com.financiaplus.backend.customer.dto;

import java.time.LocalDate;

public record CustomerGeneralResponse(
        boolean existing,
        String fullName,
        String identityNumber,
        String address,
        LocalDate birthDate,
        String gender,
        String email,
        String phone
) {
}