package com.financiaplus.backend.aml;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aml")
@RequiredArgsConstructor
public class AmlController {

    private final AmlService amlService;

    @GetMapping("/by-document/{document}")
    public AmlResponse searchByDocument(
            @PathVariable String document
    ) {
        return amlService.searchByDocument(document);
    }

    @GetMapping("/by-name")
    public AmlResponse searchByName(
            @RequestParam String name
    ) {
        return amlService.searchByName(name);
    }
}