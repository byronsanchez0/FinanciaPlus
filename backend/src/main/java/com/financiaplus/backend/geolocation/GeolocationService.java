package com.financiaplus.backend.geolocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Service
public class GeolocationService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    GeolocationService.class
            );

    private final RestClient restClient;

    public GeolocationService(
            RestClient.Builder builder
    ) {
        this.restClient = builder
                .baseUrl("https://ipapi.co")
                .build();
    }

    public Optional<IpApiResponse> locate(
            String clientIp
    ) {
        String path = isLocalAddress(clientIp)
                ? "/json/"
                : "/" + clientIp + "/json/";

        try {
            IpApiResponse response = restClient
                    .get()
                    .uri(path)
                    .retrieve()
                    .body(IpApiResponse.class);

            return Optional.ofNullable(response);
        } catch (RestClientException exception) {
            log.warn(
                    "No fue posible obtener la geolocalización: {}",
                    exception.getMessage()
            );

            return Optional.empty();
        }
    }

    private boolean isLocalAddress(String ip) {
        return ip == null
                || ip.isBlank()
                || ip.equals("127.0.0.1")
                || ip.equals("0:0:0:0:0:0:0:1")
                || ip.equals("::1");
    }
}