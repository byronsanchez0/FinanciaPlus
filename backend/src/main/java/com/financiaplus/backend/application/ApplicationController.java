package com.financiaplus.backend.application;

import com.financiaplus.backend.application.dto.ApplicationRequest;
import com.financiaplus.backend.application.dto.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse create(
            @RequestBody ApplicationRequest request
    ) {
        return applicationService.create(request);
    }

    @GetMapping("/{id}")
    public ApplicationResponse findById(
            @PathVariable UUID id
    ) {
        return applicationService.findById(id);
    }

    @PatchMapping("/{id}")
    public ApplicationResponse update(
            @PathVariable UUID id,
            @RequestBody ApplicationRequest request
    ) {
        return applicationService.update(id, request);
    }
}