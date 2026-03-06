package com.ib.web.controller.umkm;

import com.ib.web.common.PageResult;
import com.ib.web.dto.umkm.MerchantDto;
import com.ib.web.dto.umkm.OutletDto;
import com.ib.web.service.JwtService;
import com.ib.web.service.umkm.MerchantClientService;
import com.ib.web.service.umkm.OutletClientService;
import com.ib.web.util.MessageUtil;
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
@RequestMapping("/outlets")
public class OutletWebController {
    private final MerchantClientService merchantClientService;
    private final OutletClientService outletClientService;
    private final JwtService jwtService;
    private final MessageUtil messageUtil;

    public OutletWebController(MerchantClientService merchantClientService,
                               OutletClientService outletClientService,
                               JwtService jwtService, MessageUtil messageUtil) {
        this.merchantClientService = merchantClientService;
        this.outletClientService = outletClientService;
        this.jwtService = jwtService;
        this.messageUtil = messageUtil;
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("role")
    public String getRole(Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        Claims claims = jwtService.validateToken(jwt);
        return claims.get("role", String.class); // ADMIN / USER
    }

    @ModelAttribute("merchants")
    public List<MerchantDto> getMerchantsByRole(Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        return merchantClientService.getMerchantsByRole(jwt);
    }

    @GetMapping
    public String outlets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String jwt = (String) authentication.getCredentials();

        PageResult<OutletDto> result =
                outletClientService.getOutlets(jwt, page, size, keyword);

        model.addAttribute("activeMenu", "outlets");
        model.addAttribute("outlets", result.getContent());
        model.addAttribute("currentPage", result.getPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("pageSize", result.getSize());
        model.addAttribute("totalElements", result.getTotalElements());
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "umkm/outlet_list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("outlet", new OutletDto());
        model.addAttribute("mode", "add");
        model.addAttribute("self", false);
        return "umkm/outlet_form";
    }

    @PostMapping("/add")
    public String saveOutlet(HttpSession session,
           @Valid @ModelAttribute("outlet") OutletDto outlet,
           BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("mode", "add");
            return "umkm/outlet_form";
        }
        String token = (String) session.getAttribute("JWT");
        outletClientService.createOutlet(outlet, token);
        redirectAttributes.addFlashAttribute(
                "success",
                messageUtil.get("outlet.add.success")
        );
        return "redirect:/outlets";
    }

    @GetMapping("/{id}")
    public String viewOutlet(HttpSession session,
                           @PathVariable Long id,
                           Model model
    ) {
        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("JWT");
        OutletDto outlet = outletClientService.getById(id, token);
        model.addAttribute("outlet", outlet);
        model.addAttribute("mode", "view");
        model.addAttribute("self", false);
        return "umkm/outlet_form";
    }

    @GetMapping("/{id}/edit")
    public String editOutlet(HttpSession session,
                           @PathVariable Long id,
                           Model model
    ) {
        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("JWT");
        OutletDto outlet = outletClientService.getById(id, token);
        model.addAttribute("outlet", outlet);
        model.addAttribute("mode", "edit");
        model.addAttribute("self", false);
        return "umkm/outlet_form";
    }

    @PostMapping("/{id}/edit")
    public String updateOutlet(HttpSession session,
         @PathVariable Long id,
         @Valid @ModelAttribute("outlet") OutletDto outlet,
         BindingResult result, Model model,
         RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("mode", "edit");
            return "umkm/outlet_form";
        }
        String token = (String) session.getAttribute("JWT");
        outlet.setId(id);
        outletClientService.updateOutlet(id, outlet, token);
        redirectAttributes.addFlashAttribute(
                "success",
                messageUtil.get("outlet.edit.success")
        );
        return "redirect:/outlets";
    }
}
