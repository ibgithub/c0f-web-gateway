package com.ib.web.controller.pos;

import com.ib.web.dto.pos.Sales;
import com.ib.web.service.pos.SalesClientService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/receipts")
public class ApiReceiptController {

    private final SalesClientService salesClientService;

    public ApiReceiptController(SalesClientService salesClientService) {
        this.salesClientService = salesClientService;
    }
    @GetMapping("/{id}")
    public Sales getReceiptData(@PathVariable Long id,
                                Authentication authentication) {
        String token = (String) authentication.getCredentials();
        return salesClientService.getById(id, token);
    }
}
