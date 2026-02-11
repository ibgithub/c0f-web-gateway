package com.ib.web.controller;

import com.ib.web.service.AuthClientService;
import com.ib.web.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("JWT") != null) {
            return "redirect:/alumni";
        }
        return "redirect:/index";
    }

    @GetMapping({"/index"})
    public String login() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/alumnies";
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
        return "redirect:/alumni";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

//    @GetMapping({"/error"})
//    public String accessDenied(
//                               HttpServletRequest request, Model model) {
//
//        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//
//        int statusCode = 500;
//
//        if (statusObj != null) {
//            statusCode = Integer.parseInt(statusObj.toString());
//        }
//
//        model.addAttribute("statusCode", statusCode);
//
//        if (statusCode == 404) {
//            model.addAttribute("errorMessage", "Page Not Found");
//        } else if (statusCode == 403) {
//            model.addAttribute("errorMessage", "Access Denied");
//        } else {
//            model.addAttribute("errorMessage", "Unexpected Error");
//        }
//
//        return "error";
//    }

}