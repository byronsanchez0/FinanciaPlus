package com.financiaplus.backend.evaluation;

import com.financiaplus.backend.application.*;
import com.financiaplus.backend.application.dto.ApplicationResponse;
import com.financiaplus.backend.geolocation.GeolocationService;
import com.financiaplus.backend.geolocation.IpApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OriginationService {

    private final ApplicationService applicationService;
    private final GeolocationService geolocationService;

    @Transactional
    public ApplicationResponse originate(
            UUID applicationId,
            String clientIp
    ) {
        Application application =
                applicationService.getEntity(applicationId);

        if (application.getStatus()
                != ApplicationStatus.APPROVED) {
            throw new IllegalArgumentException(
                    "La solicitud debe estar aprobada antes de originar el producto"
            );
        }

        geolocationService.locate(clientIp)
                .ifPresent(location ->
                        applyLocation(application, location)
                );

        /*
         * La prueba indica que una falla de geolocalización
         * no debe bloquear el proceso.
         */
        application.setStatus(ApplicationStatus.COMPLETED);

        return ApplicationResponse.from(
                applicationService.save(application)
        );
    }

    private void applyLocation(
            Application application,
            IpApiResponse location
    ) {
        application.setIpAddress(location.ip());
        application.setCountry(location.country());
        application.setRegion(location.region());
        application.setCity(location.city());
    }
}