package com.ib.web.controller.pos;

import com.ib.web.service.AuthClientService;
import com.ib.web.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pos")
public class PosWebController {

    private final AuthClientService authClientService;
    private final JwtService jwtService;

    public PosWebController(AuthClientService authClientService, JwtService jwtService) {
        this.authClientService = authClientService;
        this.jwtService = jwtService;
    }
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping
    public String pos(Authentication authentication) {
        return "pos/pos";
    }
}