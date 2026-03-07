package com.ib.web.controller.umkm;

import com.ib.web.common.PageResult;
import com.ib.web.dto.UserDto;
import com.ib.web.dto.umkm.MerchantDto;
import com.ib.web.dto.umkm.OutletDto;
import com.ib.web.dto.umkm.OutletStaffDto;
import com.ib.web.service.AuthUserClient;
import com.ib.web.service.JwtService;
import com.ib.web.service.umkm.MerchantClientService;
import com.ib.web.service.umkm.OutletClientService;
import com.ib.web.service.umkm.OutletStaffClientService;
import com.ib.web.util.Constants;
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
@RequestMapping("/outlet-staffs")
public class OutletStaffWebController {
    private final MerchantClientService merchantClientService;
    private final OutletClientService outletClientService;
    private final AuthUserClient authUserClient;
    private final OutletStaffClientService outletStaffClientService;
    private final JwtService jwtService;
    private final MessageUtil messageUtil;

    public OutletStaffWebController(MerchantClientService merchantClientService,
                                    OutletClientService outletClientService, AuthUserClient authUserClient,
                                    OutletStaffClientService outletStaffClientService,
                                    JwtService jwtService, MessageUtil messageUtil) {
        this.merchantClientService = merchantClientService;
        this.outletClientService = outletClientService;
        this.authUserClient = authUserClient;
        this.outletStaffClientService = outletStaffClientService;
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
    @ModelAttribute("outlets")
    public List<OutletDto> getOutletsByRole(Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        return outletClientService.getOutletsByRole(jwt);
    }

    @ModelAttribute("merchantUsers")
    public List<UserDto> populateMerchantOwners(Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        return authUserClient.getUsersByRole(jwt, Constants.MERCHANT_USER);
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

        PageResult<OutletStaffDto> result =
                outletStaffClientService.getOutletStaffs(jwt, page, size, keyword);

        model.addAttribute("activeMenu", "outlets");
        model.addAttribute("outletStaffs", result.getContent());
        model.addAttribute("currentPage", result.getPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("pageSize", result.getSize());
        model.addAttribute("totalElements", result.getTotalElements());
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "umkm/outlet_staff_list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("outletStaff", new OutletStaffDto());
        model.addAttribute("mode", "add");
        model.addAttribute("self", false);
        return "umkm/outlet_staff_form";
    }

    @PostMapping("/add")
    public String saveOutletStaff(HttpSession session,
           @Valid @ModelAttribute("outletStaff") OutletStaffDto outletStaff,
           BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("mode", "add");
            return "umkm/outlet_staff_form";
        }
        String token = (String) session.getAttribute("JWT");
        outletStaffClientService.createOutletStaff(outletStaff, token);
        redirectAttributes.addFlashAttribute(
                "success",
                messageUtil.get("outletStaff.add.success")
        );
        return "redirect:/outlet-staffs";
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
        OutletStaffDto outletStaff = outletStaffClientService.getById(id, token);
        model.addAttribute("outletStaff", outletStaff);
        model.addAttribute("mode", "view");
        model.addAttribute("self", false);
        return "umkm/outlet_staff_form";
    }

    @GetMapping("/{id}/edit")
    public String editOutletStaff(HttpSession session,
                           @PathVariable Long id,
                           Model model
    ) {
        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("JWT");
        OutletStaffDto outletStaff = outletStaffClientService.getById(id, token);
        model.addAttribute("outletStaff", outletStaff);
        model.addAttribute("mode", "edit");
        model.addAttribute("self", false);
        return "umkm/outlet_staff_form";
    }

    @PostMapping("/{id}/edit")
    public String updateOutlet(HttpSession session,
         @PathVariable Long id,
         @Valid @ModelAttribute("outletStaff") OutletStaffDto outletStaff,
         BindingResult result, Model model,
         RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("mode", "edit");
            return "umkm/outlet_staff_form";
        }
        String token = (String) session.getAttribute("JWT");
        outletStaff.setId(id);
        outletStaffClientService.updateOutletStaff(id, outletStaff, token);
        redirectAttributes.addFlashAttribute(
                "success",
                messageUtil.get("outletStaff.edit.success")
        );
        return "redirect:/outlet-staffs";
    }
}
