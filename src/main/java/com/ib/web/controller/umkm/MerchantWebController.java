package com.ib.web.controller.umkm;

import com.ib.web.common.PageResult;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @ModelAttribute("role")
    public String getRole(Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        Claims claims = jwtService.validateToken(jwt);
        return claims.get("role", String.class); // ADMIN / USER
    }

    @GetMapping
    public String merchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String jwt = (String) authentication.getCredentials();

        PageResult<MerchantDto> result =
                merchantClientService.getMerchants(jwt, page, size, keyword);

        model.addAttribute("merchants", result.getContent());
        model.addAttribute("currentPage", result.getPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("pageSize", result.getSize());
        model.addAttribute("totalElements", result.getTotalElements());
        model.addAttribute("keyword", keyword == null ? "" : keyword);
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
    public String viewMerchant(
                           @PathVariable Long id,
                           Model model,
                           Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String jwt = (String) authentication.getCredentials();
        MerchantDto merchant = merchantClientService.getById(id, jwt);

        model.addAttribute("merchant", merchant);
        model.addAttribute("mode", "view");
        model.addAttribute("self", false);
        return "umkm/merchant_form";
    }

    /* ======================
       EDIT MERCHANT (FORM)
       ====================== */
    @GetMapping("/{id}/edit")
    public String editMerchant(
                           @PathVariable Long id,
                           Model model,
                           Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String jwt = (String) authentication.getCredentials();
        MerchantDto merchant = merchantClientService.getById(id, jwt);

        model.addAttribute("merchant", merchant);
        model.addAttribute("mode", "edit");
        model.addAttribute("self", false);
        return "umkm/merchant_form";
    }

    /* ======================
       SAVE MERCHANT
       ====================== */
    @PostMapping("/{id}/edit")
    public String updateMerchant(
         @PathVariable Long id, Model model,
         @Valid @ModelAttribute("merchant") MerchantDto merchant, BindingResult result,
         Authentication authentication
    ) {
        if (result.hasErrors()) {
            model.addAttribute("mode", "edit");
            model.addAttribute("self", false);
            return "umkm/merchant_form";
        }
        String jwt = (String) authentication.getCredentials();
        merchant.setId(id);
        merchantClientService.updateMerchant(id, merchant, jwt);
        return "redirect:/merchants";
    }
    // ===============================
    // DELETE MERCHANT
    // ===============================
    @PostMapping("/{id}/delete")
    public String deleteMerchant(
                                 @PathVariable Long id,
                                 RedirectAttributes redirectAttributes,
                                 Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String jwt = (String) authentication.getCredentials();
        merchantClientService.deleteMerchant(id, jwt);
        redirectAttributes.addFlashAttribute("success", "Pedagang berhasil dihapus.");
        return "redirect:/merchants";
    }
}
