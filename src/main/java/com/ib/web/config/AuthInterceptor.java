package com.ib.web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        String uri = request.getRequestURI();

//        // 🔑 INI PENTING: allow GET & POST /login
//        if (uri.equals("/login")) {
//            return true;
//        }
//
//        HttpSession session = request.getSession(false);
//        boolean loggedIn = session != null && session.getAttribute("JWT") != null;
//
//        if (!loggedIn) {
//            response.sendRedirect("/login");
//            return false;
//        }

        return true;
    }
}