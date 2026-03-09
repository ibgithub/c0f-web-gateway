package com.ib.web.controller.pos;

import com.ib.web.dto.pos.SalesCreateRequest;
import com.ib.web.service.pos.SalesClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class ApiSalesController {
    private final SalesClientService salesClientService;

    public ApiSalesController(SalesClientService salesClientService) {
        this.salesClientService = salesClientService;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody SalesCreateRequest req,
            Authentication authentication
    ) {

        String token = (String) authentication.getCredentials();

        Long salesId = salesClientService.createSales(req, token);

        return ResponseEntity.ok(Map.of("id", salesId));
    }

}
