package com.ib.web.controller.pos;

import com.ib.web.service.AuthClientService;
import com.ib.web.service.JwtService;
import com.ib.web.service.umkm.CategoryClientService;
import com.ib.web.service.umkm.ProductClientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cashier")
public class CashierWebController {

    private final AuthClientService authClientService;
    private final JwtService jwtService;
    private final CategoryClientService categoryClientService;
    private final ProductClientService productClientService;

    public CashierWebController(AuthClientService authClientService, JwtService jwtService,
                                CategoryClientService categoryClientService, ProductClientService productClientService) {
        this.authClientService = authClientService;
        this.jwtService = jwtService;
        this.categoryClientService = categoryClientService;
        this.productClientService = productClientService;
    }
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping
    public String cashier(Authentication authentication) {

        return "pos/cashier";
    }
}