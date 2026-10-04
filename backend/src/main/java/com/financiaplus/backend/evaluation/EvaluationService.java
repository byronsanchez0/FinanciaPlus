package com.financiaplus.backend.evaluation;

import com.financiaplus.backend.aml.AmlResponse;
import com.financiaplus.backend.aml.AmlService;
import com.financiaplus.backend.application.*;
import com.financiaplus.backend.application.dto.ApplicationResponse;
import com.financiaplus.backend.customer.CustomerService;
import com.financiaplus.backend.customer.dto.CustomerFinancialResponse;
import com.financiaplus.backend.customer.dto.CustomerGeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private static final BigDecimal MINIMUM_SCORE =
            new BigDecimal("7.0");

    private final ApplicationService applicationService;
    private final AmlService amlService;
    private final CustomerService customerService;

    @Transactional
    public ApplicationResponse evaluate(UUID applicationId) {
        Application application =
                applicationService.getEntity(applicationId);

        requireIdentityNumber(application);

        AmlResponse documentMatch =
                amlService.searchByDocument(
                        application.getIdentityNumber()
                );

        AmlResponse nameMatch =
                application.getFullName() == null
                        ? null
                        : amlService.searchByName(
                                application.getFullName()
                        );

        if (documentMatch.matched()
                || (nameMatch != null && nameMatch.matched())) {

            application.setStatus(
                    ApplicationStatus.REJECTED_AML
            );

            return saveAndConvert(application);
        }

        CustomerGeneralResponse customer =
                customerService.findGeneralInformation(
                        application.getIdentityNumber()
                );

        if (customer.existing()) {
            autofillCustomerInformation(application, customer);
        }

        applicationService.validateBasicInformation(application);

        CustomerFinancialResponse financial =
                customerService.findFinancialInformation(
                        application.getIdentityNumber()
                );

        if (!financial.existing()
                || financial.creditScore() == null) {
            throw new IllegalArgumentException(
                    "No existe información de score para el documento"
            );
        }

        application.setCreditScore(financial.creditScore());

        if (financial.creditScore()
                .compareTo(MINIMUM_SCORE) < 0) {

            application.setStatus(
                    ApplicationStatus.REJECTED_SCORE
            );
        } else {
            application.setStatus(
                    ApplicationStatus.APPROVED
            );
        }

        return saveAndConvert(application);
    }

    private void autofillCustomerInformation(
            Application application,
            CustomerGeneralResponse customer
    ) {
        application.setFullName(customer.fullName());
        application.setAddress(customer.address());
        application.setBirthDate(customer.birthDate());
        application.setGender(customer.gender());
        application.setEmail(customer.email());
        application.setPhone(customer.phone());
    }

    private void requireIdentityNumber(
            Application application
    ) {
        if (application.getIdentityNumber() == null
                || application.getIdentityNumber().isBlank()) {
            throw new IllegalArgumentException(
                    "El documento de identidad es obligatorio"
            );
        }
    }

    private ApplicationResponse saveAndConvert(
            Application application
    ) {
        return ApplicationResponse.from(
                applicationService.save(application)
        );
    }
}