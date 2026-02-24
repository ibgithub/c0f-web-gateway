package com.ib.web.controller;

import com.ib.web.service.AuthClientService;
import com.ib.web.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LoginController {

    private final AuthClientService authClientService;
    private final JwtService jwtService;

    public LoginController(AuthClientService authClientService, JwtService jwtService) {
        this.authClientService = authClientService;
        this.jwtService = jwtService;
    }
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("JWT") != null) {
            return "redirect:/index";
        }
        return "redirect:/index";
    }

    @GetMapping({"/index"})
    public String login() {
        System.out.println("Current Locale = " + LocaleContextHolder.getLocale());
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/index";
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request
    ) {
        String jwt = authClientService.login(username, password);

        Claims claims = jwtService.validateToken(jwt);

        String user = claims.getSubject();          // username
        String role = claims.get("role", String.class); // ADMIN / USER

        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + role));

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        user,
                        jwt,
                        authorities
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);

        request.getSession(true)
                .setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        context
                );

        request.getSession().setAttribute("JWT", jwt);
        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}