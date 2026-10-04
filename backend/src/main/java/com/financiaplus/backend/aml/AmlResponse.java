package com.financiaplus.backend.aml;

import java.time.LocalDate;

public record AmlResponse(
        boolean matched,
        String fullName,
        String identityNumber,
        LocalDate birthDate,
        String message
) {
}