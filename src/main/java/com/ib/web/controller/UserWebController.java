package com.ib.web.controller;

import com.ib.web.dto.UserDto;
import com.ib.web.service.AuthUserClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserWebController {

    private final AuthUserClient authUserClient;

    public UserWebController(AuthUserClient authUserClient) {
        this.authUserClient = authUserClient;
    }

    // ===============================
    // USER LIST (ADMIN)
    // ===============================
    @GetMapping
    public String listUsers(HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT");

        List<UserDto> users = authUserClient.getUsers(token);
        model.addAttribute("users", users);

        return "users/list";
    }

    // ===============================
    // ADD USER FORM
    // ===============================
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "users/add";
    }

    // ===============================
    // SAVE USER
    // ===============================
    @PostMapping("/add")
    public String saveUser(UserDto user, HttpSession session) {
        String token = (String) session.getAttribute("JWT");
        authUserClient.createUser(user, token);
        return "redirect:/users";
    }

    // ===============================
    // MY PROFILE
    // ===============================
    @GetMapping("/me")
    public String myProfile(HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT");
        UserDto me = authUserClient.getMe(token);
        model.addAttribute("user", me);
        return "profile/index";
    }

    // ===============================
    // UPDATE MY PROFILE
    // ===============================
    @PostMapping("/me")
    public String updateProfile(UserDto user, HttpSession session) {
        String token = (String) session.getAttribute("JWT");
        Long userId = (Long) session.getAttribute("USER_ID");

        authUserClient.updateUser(userId, user, token);
        return "redirect:/users/me";
    }
}
