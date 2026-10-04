package com.financiaplus.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-API-Key";

    private final String expectedApiKey;

    public ApiKeyFilter(
            @Value("${security.customer-api-key}")
            String expectedApiKey
    ) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {
        return !request.getRequestURI()
                .startsWith("/api/customers");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String receivedApiKey = request.getHeader(HEADER_NAME);

        if (!validApiKey(receivedApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    """
                    {
                      "error": "API Key inválida o no proporcionada"
                    }
                    """
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean validApiKey(String receivedApiKey) {
        if (receivedApiKey == null) {
            return false;
        }

        byte[] expected = expectedApiKey.getBytes(
                StandardCharsets.UTF_8
        );

        byte[] received = receivedApiKey.getBytes(
                StandardCharsets.UTF_8
        );

        return MessageDigest.isEqual(expected, received);
    }
}