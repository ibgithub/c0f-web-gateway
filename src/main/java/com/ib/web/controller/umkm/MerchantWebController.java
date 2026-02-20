package com.ib.web.controller.umkm;

import com.ib.web.dto.UserDto;
import com.ib.web.dto.umkm.MerchantDto;
import com.ib.web.service.AuthUserClient;
import com.ib.web.service.JwtService;
import com.ib.web.service.umkm.MerchantClientService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/merchants")
public class MerchantWebController {

    private final MerchantClientService merchantClientService;
    private final AuthUserClient authUserClient;
    private final JwtService jwtService;

    public MerchantWebController(MerchantClientService merchantClientService, AuthUserClient authUserClient, JwtService jwtService) {
        this.merchantClientService = merchantClientService;
        this.authUserClient = authUserClient;
        this.jwtService = jwtService;
    }

    @GetMapping
    public String merchants(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String jwt = (String) authentication.getCredentials();

        Claims claims = jwtService.validateToken(jwt);

        String role = claims.get("role", String.class); // ADMIN / USER

        model.addAttribute("activeMenu", "merchants");
        model.addAttribute("title", "Merchants");
        model.addAttribute("role", role);
        List<MerchantDto> merchants = merchantClientService.getMerchants(jwt);
        model.addAttribute("merchants", merchants);

        return "umkm/merchant_list";
    }

    // ===============================
    // ADD MERCHANT FORM
    // ===============================
    @GetMapping("/add")
    public String addForm(Model model, Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        List<UserDto> users = authUserClient.getUsersByRole(jwt, "USER");

        model.addAttribute("users", users);
        model.addAttribute("merchant", new MerchantDto());
        model.addAttribute("mode", "add");
        model.addAttribute("self", false);
        return "umkm/merchant_form";
    }

    // ===============================
    // ADD MERCHANT POST
    // ===============================
    @PostMapping("/add")
    public String saveUser(HttpSession session, MerchantDto merchant) {
        String token = (String) session.getAttribute("JWT");
        merchantClientService.createMerchant(merchant, token);
        return "redirect:/merchants";
    }

    /* ======================
       VIEW MERCHANT (READ ONLY)
       ====================== */
    @GetMapping("/{id}")
    public String viewMerchant(HttpSession session,
                           @PathVariable Long id,
                           Model model
    ) {
        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("JWT");
        MerchantDto merchant = merchantClientService.getById(id, token);
        List<UserDto> users = authUserClient.getUsersByRole(token, "USER");

        model.addAttribute("users", users);
        model.addAttribute("merchant", merchant);
        model.addAttribute("mode", "view");
        model.addAttribute("self", false);
        return "umkm/merchant_form";
    }

    /* ======================
       EDIT MERCHANT (FORM)
       ====================== */
    @GetMapping("/{id}/edit")
    public String editMerchant(HttpSession session,
                           @PathVariable Long id,
                           Model model
    ) {
        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("JWT");
        MerchantDto merchant = merchantClientService.getById(id, token);
        List<UserDto> users = authUserClient.getUsersByRole(token, "USER");

        model.addAttribute("users", users);
        model.addAttribute("merchant", merchant);
        model.addAttribute("mode", "edit");
        model.addAttribute("self", false);
        return "umkm/merchant_form";
    }

    /* ======================
       SAVE MERCHANT
       ====================== */
    @PostMapping("/{id}/edit")
    public String updateMerchant(HttpSession session,
                             @PathVariable Long id,
                             @ModelAttribute MerchantDto merchant
    ) {
        String token = (String) session.getAttribute("JWT");

        merchant.setId(id);
        merchantClientService.updateMerchant(id, merchant, token);
        return "redirect:/merchants";
    }
}
