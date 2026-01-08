package com.ib.web.controller;

import com.ib.web.service.AuthClientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final AuthClientService authClientService;

    public LoginController(AuthClientService authClientService) {
        this.authClientService = authClientService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session
    ) {
        String jwt = authClientService.login(username, password);
        session.setAttribute("JWT", jwt);
        return "redirect:/members";
    }
//    @GetMapping("/logout")
//    public String logout(HttpSession session) {
//        session.invalidate();
//        return "redirect:/login";
//    }
}