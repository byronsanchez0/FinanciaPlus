package com.financiaplus.backend.geolocation;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IpApiResponse(
        String ip,
        @JsonProperty("country_name")
        String country,
        String region,
        String city
) {
}