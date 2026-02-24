package com.ib.web.controller.umkm;

import com.ib.web.dto.UserDto;
import com.ib.web.dto.umkm.MerchantDto;
import com.ib.web.service.AuthUserClient;
import com.ib.web.service.JwtService;
import com.ib.web.service.umkm.MerchantClientService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("users")
    public List<UserDto> populateUsers(Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        return authUserClient.getUsersByRole(jwt, "USER");
    }

    @ModelAttribute("merchants")
    public List<MerchantDto> populateMerchants(Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        return merchantClientService.getMerchants(jwt);
    }

    @GetMapping
    public String merchants(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String jwt = (String) authentication.getCredentials();
        Claims claims = jwtService.validateToken(jwt);
        String role = claims.get("role", String.class); // ADMIN / USER
        model.addAttribute("role", role);
        return "umkm/merchant_list";
    }

    // ===============================
    // ADD MERCHANT FORM
    // ===============================
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("merchant", new MerchantDto());
        model.addAttribute("mode", "add");
        model.addAttribute("self", false);
        return "umkm/merchant_form";
    }

    // ===============================
    // ADD MERCHANT POST
    // ===============================
    @PostMapping("/add")
    public String saveMerchant(HttpSession session, Model model,
                           @Valid @ModelAttribute("merchant") MerchantDto merchant, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("mode", "add");
            model.addAttribute("self", false);
            return "umkm/merchant_form";
        }
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
         @PathVariable Long id, Model model,
         @Valid @ModelAttribute("merchant") MerchantDto merchant, BindingResult result
    ) {
        if (result.hasErrors()) {
            model.addAttribute("mode", "edit");
            model.addAttribute("self", false);
            return "umkm/merchant_form";
        }
        String token = (String) session.getAttribute("JWT");
        merchant.setId(id);
        merchantClientService.updateMerchant(id, merchant, token);
        return "redirect:/merchants";
    }
}
