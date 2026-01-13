package com.ib.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class HomeController {

    private final RestTemplate restTemplate;

    public HomeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping({"", "/"})
    public String getHomePage() {
        return "index";
    }

    @GetMapping("/member")
    public String getMemberPage() {
        return "member";
    }

    @GetMapping("/koperasi")
    public String getKoperasiPage() {
        return "koperasi";
    }

//    @GetMapping("/")
//    public String home(Model model, HttpSession session) {
//        if (session.getAttribute("user") == null) {
//            return "redirect:/login";
//        }
//
//        MemberDto[] members =
//                restTemplate.getForObject(
//                        "http://localhost:8082/api/members",
//                        MemberDto[].class
//                );
//
//        model.addAttribute("members", members);
//        return "home";
//    }
}