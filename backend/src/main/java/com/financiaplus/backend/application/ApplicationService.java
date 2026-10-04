package com.financiaplus.backend.application;

import com.financiaplus.backend.application.dto.ApplicationRequest;
import com.financiaplus.backend.application.dto.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private static final Pattern IDENTITY_PATTERN =
            Pattern.compile("^1234.*$");

    private final ApplicationRepository applicationRepository;

    public ApplicationResponse create(ApplicationRequest request) {
        Application application = new Application();
        updateFields(application, request);
        validateIdentity(application.getIdentityNumber(), null);

        return ApplicationResponse.from(
                applicationRepository.save(application)
        );
    }

    public ApplicationResponse findById(UUID id) {
        return ApplicationResponse.from(getEntity(id));
    }

    public ApplicationResponse update(
            UUID id,
            ApplicationRequest request
    ) {
        Application application = getEntity(id);
        updateFields(application, request);
        validateIdentity(application.getIdentityNumber(), id);

        return ApplicationResponse.from(
                applicationRepository.save(application)
        );
    }

    public Application getEntity(UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "No existe la solicitud " + id
                        )
                );
    }

    public Application save(Application application) {
        return applicationRepository.save(application);
    }

    public void validateBasicInformation(Application application) {
        requireText(application.getFullName(), "nombre");
        requireText(application.getAddress(), "dirección");
        requireText(application.getGender(), "género");
        requireText(
                application.getIdentityNumber(),
                "documento de identidad"
        );
        requireText(application.getEmail(), "correo");
        requireText(application.getPhone(), "teléfono");

        if (application.getBirthDate() == null) {
            throw new IllegalArgumentException(
                    "La fecha de nacimiento es obligatoria"
            );
        }
    }

    private void updateFields(
            Application application,
            ApplicationRequest request
    ) {
        if (request.fullName() != null) {
            application.setFullName(request.fullName());
        }

        if (request.address() != null) {
            application.setAddress(request.address());
        }

        if (request.birthDate() != null) {
            application.setBirthDate(request.birthDate());
        }

        if (request.gender() != null) {
            application.setGender(request.gender());
        }

        if (request.identityNumber() != null) {
            application.setIdentityNumber(
                    normalizeIdentity(request.identityNumber())
            );
        }

        if (request.email() != null) {
            application.setEmail(request.email());
        }

        if (request.phone() != null) {
            application.setPhone(request.phone());
        }

        if (request.biometricScore() != null) {
            application.setBiometricScore(request.biometricScore());
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo " + fieldName + " es obligatorio"
            );
        }
    }

    private void validateIdentity(
            String identityNumber,
            UUID currentApplicationId
    ) {
        requireText(identityNumber, "documento de identidad");

        if (!IDENTITY_PATTERN.matcher(identityNumber).matches()) {
            throw new IllegalArgumentException(
                    "Documento no válido: los primeros 4 caracteres deben ser 1234"
            );
        }

        boolean duplicated = currentApplicationId == null
                ? applicationRepository
                        .existsByIdentityNumberIgnoreCase(identityNumber)
                : applicationRepository
                        .existsByIdentityNumberIgnoreCaseAndIdNot(
                                identityNumber,
                                currentApplicationId
                        );

        if (duplicated) {
            throw new IllegalArgumentException(
                    "Ya existe una solicitud para este documento"
            );
        }
    }

    private String normalizeIdentity(String identityNumber) {
        return identityNumber.trim().toUpperCase(Locale.ROOT);
    }
}
