package com.ib.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CrowdFundingController {

    @GetMapping("/crowdfunding")
    public String crowdfunding(Model model, HttpSession session) {

        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }

        model.addAttribute("title", "Crowd Funding");
        return "crowdfunding/index";
    }
}

