package com.ib.web.controller.pos;

import com.ib.web.dto.pos.Sales;
import com.ib.web.service.AuthClientService;
import com.ib.web.service.JwtService;
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

    private final AuthClientService authClientService;
    private final JwtService jwtService;
    private final SalesClientService salesClientService;

    public SalesWebController(AuthClientService authClientService, JwtService jwtService,
                              SalesClientService salesClientService) {
        this.authClientService = authClientService;
        this.jwtService = jwtService;
        this.salesClientService = salesClientService;
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
}