package com.ib.web.controller;

import com.ib.web.dto.MemberDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class HomeController {

    private final RestTemplate restTemplate;

    public HomeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        MemberDto[] members =
                restTemplate.getForObject(
                        "http://localhost:8082/api/members",
                        MemberDto[].class
                );

        model.addAttribute("members", members);
        return "home";
    }
}