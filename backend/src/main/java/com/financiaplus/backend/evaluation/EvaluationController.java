package com.financiaplus.backend.evaluation;

import com.financiaplus.backend.application.dto.ApplicationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final OriginationService originationService;

    @PostMapping("/{id}/evaluate")
    public ApplicationResponse evaluate(
            @PathVariable UUID id
    ) {
        return evaluationService.evaluate(id);
    }

    @PostMapping("/{id}/originate")
    public ApplicationResponse originate(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        String clientIp = extractClientIp(request);

        return originationService.originate(
                id,
                clientIp
        );
    }

    private String extractClientIp(
            HttpServletRequest request
    ) {
        String forwardedFor =
                request.getHeader("X-Forwarded-For");

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}