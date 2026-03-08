package com.ib.web.controller.pos;

import com.ib.web.service.AuthClientService;
import com.ib.web.service.JwtService;
import com.ib.web.service.umkm.CategoryClientService;
import com.ib.web.service.umkm.ProductClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String cashier(HttpSession session, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String jwt = (String) authentication.getCredentials();
        Long merchantId = (Long) session.getAttribute("merchantId");

        model.addAttribute("showCashierModal", merchantId == null);

        if (merchantId != null) {

            var products = productClientService.findByMerchantId(jwt, merchantId);

            model.addAttribute("products", products);

            var categories = categoryClientService.findByMerchantId(jwt, merchantId);

            model.addAttribute("categories", categories);
        }

        return "pos/cashier";
    }
    @PostMapping("/select-outlet")
    @ResponseBody
    public void selectOutlet(
            @RequestParam Long merchantId,
            @RequestParam Long outletId,
            HttpSession session) {

        session.setAttribute("merchantId", merchantId);
        session.setAttribute("outletId", outletId);
    }
}