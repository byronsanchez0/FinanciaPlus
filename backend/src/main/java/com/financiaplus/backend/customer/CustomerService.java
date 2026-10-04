package com.financiaplus.backend.customer;

import com.financiaplus.backend.customer.dto.CustomerFinancialResponse;
import com.financiaplus.backend.customer.dto.CustomerGeneralResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

@Service
public class CustomerService {

    private final Map<String, CustomerData> customers = Map.of(
            "1234-ALTO",
            new CustomerData(
                    "Cliente Aprobado",
                    "1234-ALTO",
                    "Zona 10, Ciudad de Guatemala",
                    LocalDate.of(1992, 5, 12),
                    "Masculino",
                    "aprobado@example.com",
                    "55550001",
                    new BigDecimal("8.50"),
                    new BigDecimal("15000.00"),
                    new BigDecimal("1200.00")
            ),

            "1234-BAJO",
            new CustomerData(
                    "Cliente Score Bajo",
                    "1234-BAJO",
                    "Zona 1, Ciudad de Guatemala",
                    LocalDate.of(1988, 11, 3),
                    "Femenino",
                    "scorebajo@example.com",
                    "55550002",
                    new BigDecimal("6.40"),
                    new BigDecimal("5000.00"),
                    new BigDecimal("3500.00")
            )
    );

    public CustomerGeneralResponse findGeneralInformation(
            String document
    ) {
        CustomerData customer = customers.get(normalizeDocument(document));

        if (customer == null) {
            return new CustomerGeneralResponse(
                    false,
                    null,
                    document,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        return new CustomerGeneralResponse(
                true,
                customer.fullName(),
                customer.identityNumber(),
                customer.address(),
                customer.birthDate(),
                customer.gender(),
                customer.email(),
                customer.phone()
        );
    }

    public CustomerFinancialResponse findFinancialInformation(
            String document
    ) {
        CustomerData customer = customers.get(normalizeDocument(document));

        if (customer == null) {
            return new CustomerFinancialResponse(
                    false,
                    document,
                    null,
                    null,
                    null
            );
        }

        return new CustomerFinancialResponse(
                true,
                customer.identityNumber(),
                customer.creditScore(),
                customer.monthlyIncome(),
                customer.currentDebt()
        );
    }

    private record CustomerData(
            String fullName,
            String identityNumber,
            String address,
            LocalDate birthDate,
            String gender,
            String email,
            String phone,
            BigDecimal creditScore,
            BigDecimal monthlyIncome,
            BigDecimal currentDebt
    ) {
    }

    private String normalizeDocument(String document) {
        return document.trim().toUpperCase(Locale.ROOT);
    }
}
