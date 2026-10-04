package com.financiaplus.backend.customer;

import com.financiaplus.backend.customer.dto.CustomerFinancialResponse;
import com.financiaplus.backend.customer.dto.CustomerGeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{document}/general")
    public CustomerGeneralResponse findGeneralInformation(
            @PathVariable String document
    ) {
        return customerService.findGeneralInformation(document);
    }

    @GetMapping("/{document}/financial")
    public CustomerFinancialResponse findFinancialInformation(
            @PathVariable String document
    ) {
        return customerService.findFinancialInformation(document);
    }
}