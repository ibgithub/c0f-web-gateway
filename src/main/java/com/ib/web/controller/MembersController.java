package com.ib.web.controller;

import com.ib.web.dto.MemberDto;
import com.ib.web.security.JwtUtil;
import com.ib.web.service.MemberClientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class MembersController {

    private final MemberClientService memberClientService;
    private final JwtUtil jwtUtil;

    public MembersController(MemberClientService memberClientService,
                             JwtUtil jwtUtil) {
        this.memberClientService = memberClientService;
        this.jwtUtil = jwtUtil;
    }
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
    @GetMapping("/alumni")
    public String alumnies(Model model, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String jwt = (String) authentication.getCredentials();
        String username = authentication.getName();

        model.addAttribute("activeMenu", "members");
        model.addAttribute("title", "Members");
        model.addAttribute("username", username);
        List<MemberDto> memberDtos = memberClientService.getMembers(jwt);
        model.addAttribute(
                "members", memberDtos
        );
        return "alumni/index";
    }

}
