package com.financiaplus.backend.customer.dto;

import java.math.BigDecimal;

public record CustomerFinancialResponse(
        boolean existing,
        String identityNumber,
        BigDecimal creditScore,
        BigDecimal monthlyIncome,
        BigDecimal currentDebt
) {
}