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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

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

    // ===============================
    // LIST MERCHANT (Search + Pagination)
    // ===============================
    @GetMapping
    public String merchants(
            Model model,
            Authentication authentication,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String jwt = (String) authentication.getCredentials();
        Claims claims = jwtService.validateToken(jwt);
        String role = claims.get("role", String.class);

        // Ambil semua merchant lalu filter & paginate di sisi web-gateway
        // (Ganti dengan server-side API call jika backend sudah support pagination)
        List<MerchantDto> allMerchants = merchantClientService.getMerchants(jwt);

        // Filter by search keyword (case-insensitive)
        List<MerchantDto> filtered = allMerchants.stream()
                .filter(m -> search.isBlank() ||
                        m.getName().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());

        // Hitung total
        long total    = filtered.size();

        // Pagination manual
        int totalPages  = (int) Math.max(1, Math.ceil((double) total / size));
        int safePage    = Math.max(1, Math.min(page, totalPages));
        int fromIndex   = (safePage - 1) * size;
        int toIndex     = Math.min(fromIndex + size, (int) total);
        List<MerchantDto> paged = filtered.subList(fromIndex, toIndex);

        model.addAttribute("role",          role);
        model.addAttribute("merchants",     paged);
        model.addAttribute("search",        search);
        model.addAttribute("size",          size);
        model.addAttribute("currentPage",   safePage);
        model.addAttribute("totalPages",    totalPages);
        model.addAttribute("totalElements", total);

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
    // ===============================
    // DELETE MERCHANT
    // ===============================
    @PostMapping("/{id}/delete")
    public String deleteMerchant(HttpSession session,
                                 @PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        if (session.getAttribute("JWT") == null) return "redirect:/login";
        String token = (String) session.getAttribute("JWT");
        merchantClientService.deleteMerchant(id, token);
        redirectAttributes.addFlashAttribute("success", "Pedagang berhasil dihapus.");
        return "redirect:/merchants";
    }
}
