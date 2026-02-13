package com.ib.web.controller.umkm;

import com.ib.web.dto.umkm.MerchantDto;
import com.ib.web.service.umkm.MerchantClientService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MerchantController {

    private final MerchantClientService merchantClientService;

    public MerchantController(MerchantClientService merchantClientService) {
        this.merchantClientService = merchantClientService;
    }

    @GetMapping("/merchants")
    public String merchants(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String jwt = (String) authentication.getCredentials();
        String username = authentication.getName();

        model.addAttribute("activeMenu", "merchants");
        model.addAttribute("title", "Merchants");
        model.addAttribute("username", username);
        List<MerchantDto> merchants = merchantClientService.getMerchants(jwt);
        model.addAttribute("merchants", merchants);

        return "umkm/merchant_list";
    }

}
