package com.ib.web.controller;

import com.ib.web.dto.UserDto;
import com.ib.web.service.AuthUserClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserWebController {

    private final AuthUserClient authUserClient;

    public UserWebController(AuthUserClient authUserClient) {
        this.authUserClient = authUserClient;
    }

    // ===============================
    // ADD USER FORM
    // ===============================
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "admin/users/add";
    }

    // ===============================
    // SAVE USER
    // ===============================
    @PostMapping("/add")
    public String saveUser(UserDto user, HttpSession session) {
        String token = (String) session.getAttribute("JWT");
        authUserClient.createUser(user, token);
        return "redirect:/admin/users";
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
        return "redirect:/admin/users/me";
    }

    // ===============================
    // USER LIST (ADMIN)
    // ===============================
    @GetMapping
    public String listUsers(HttpSession session, Model model) {
        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("JWT");

        List<UserDto> users = authUserClient.getUsers(token);
        model.addAttribute("users", users);

        return "admin/user_list";
    }

    /* ======================
       VIEW USER (READ ONLY)
       ====================== */
    @GetMapping("/{id}")
    public String viewUser(HttpSession session,
                           @PathVariable Long id,
            Model model
    ) {
        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("JWT");
        UserDto user = authUserClient.getById(id, token);
        model.addAttribute("user", user);
        model.addAttribute("mode", "view");
        return "admin/user_form";
    }

    /* ======================
       EDIT USER (FORM)
       ====================== */
    @GetMapping("/{id}/edit")
    public String editUser(HttpSession session,
            @PathVariable Long id,
            Model model
    ) {
        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("JWT");
        UserDto user = authUserClient.getById(id, token);
        model.addAttribute("user", user);
        model.addAttribute("mode", "edit");
        return "admin/user_form";
    }

    /* ======================
       SAVE USER
       ====================== */
    @PostMapping("/{id}/edit")
    public String updateUser(HttpSession session,
                             @PathVariable Long id,
            @ModelAttribute UserDto user
    ) {
        String token = (String) session.getAttribute("JWT");

        user.setId(id);
        authUserClient.updateUser(id, user, token);
        return "redirect:/admin/users";
    }
}
