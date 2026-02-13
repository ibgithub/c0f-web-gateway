package com.ib.web.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        int statusCode = 500;

        if (statusObj != null) {
            statusCode = Integer.parseInt(statusObj.toString());
        }

        model.addAttribute("statusCode", statusCode);

        if (statusCode == 401) {
            model.addAttribute("errorMessage", "Unauthorized");
        } else if (statusCode == 403) {
            model.addAttribute("errorMessage", "Forbidden (Access Denied)");
        } else if (statusCode == 404) {
            model.addAttribute("errorMessage", "Page Not Found");
        } else {
            model.addAttribute("errorMessage", "Unexpected Error");
        }

        return "error";
    }
}
