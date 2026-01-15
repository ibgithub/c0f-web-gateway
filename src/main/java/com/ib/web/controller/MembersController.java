package com.ib.web.controller;

import com.ib.web.dto.MemberDto;
import com.ib.web.security.JwtUtil;
import com.ib.web.service.MemberClientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/members")
    public String members(Model model, HttpSession session) {

        String jwt = (String) session.getAttribute("JWT");

        if (jwt == null) {
            return "redirect:/login";
        }
        String username = jwtUtil.getUsername(jwt);
        model.addAttribute("activeMenu", "members");
        model.addAttribute("title", "Members");
        model.addAttribute("username", username);
        List<MemberDto> memberDtos = memberClientService.getMembers(jwt);
        model.addAttribute(
                "members", memberDtos
        );

        return "members";
    }
}
