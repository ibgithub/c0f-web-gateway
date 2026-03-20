package com.ib.web.controller.pos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ib.web.dto.pos.Sales;
import com.ib.web.service.pos.SalesClientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sales")
public class SalesWebController {

    private final SalesClientService salesClientService;
    private final ObjectMapper objectMapper;

    public SalesWebController(SalesClientService salesClientService, ObjectMapper objectMapper) {
        this.salesClientService = salesClientService;
        this.objectMapper = objectMapper;
    }
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/{id}/receipt")
    public String receipt(
            @PathVariable Long id,
            Model model, Authentication authentication
    ) {
        String token = (String) authentication.getCredentials();
        Sales sales = salesClientService.getById(id, token);

        model.addAttribute("sales", sales);

        return "pos/receipt";
    }

    @GetMapping("/{id}/receipt-qz")
    public String receiptQz(@PathVariable Long id, Model model, Authentication authentication) throws Exception {
        String token = (String) authentication.getCredentials();
        Sales sales = salesClientService.getById(id, token);
        model.addAttribute("sales", sales);
//        model.addAttribute("salesJson", objectMapper.writeValueAsString(sales));
        return "pos/receipt-qz";
    }
}