package com.ib.web.controller;

import com.ib.web.dto.LoginRequest;
import com.ib.web.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class LoginController {

    private final RestTemplate restTemplate;

    public LoginController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
//
//    @PostMapping("/login")
//    public String doLogin(
//            @RequestParam String username,
//            @RequestParam String password,
//            HttpSession session,
//            Model model
//    ) {
//        try {
//            LoginRequest req = new LoginRequest();
//            req.setUsername(username);
//            req.setPassword(password);
//
//            UserDto user = restTemplate.postForObject(
//                    "http://localhost:8081/api/auth/login",
//                    req,
//                    UserDto.class
//            );
//
//            session.setAttribute("user", user);
//            return "redirect:/";
//
//        } catch (Exception e) {
//            model.addAttribute("error", "Invalid username or password");
//            return "login";
//        }
//    }
//
//    @GetMapping("/logout")
//    public String logout(HttpSession session) {
//        session.invalidate();
//        return "redirect:/login";
//    }
}