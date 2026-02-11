package com.ib.web.controller;

import com.ib.web.dto.ChangePasswordDto;
import com.ib.web.dto.UserDto;
import com.ib.web.service.AuthUserClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
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
    public String saveUser(HttpSession session, UserDto user) {
        String token = (String) session.getAttribute("JWT");
        authUserClient.createUser(user, token);
        return "redirect:/users";
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
        model.addAttribute("self", false);
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
        model.addAttribute("self", false);
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
        return "redirect:/users";
    }

    @GetMapping("/me")
    public String myProfile(HttpSession session, Model model) {

        String token = (String) session.getAttribute("JWT");
        UserDto me = authUserClient.getMe(token);

        model.addAttribute("user", me);
        model.addAttribute("mode", "view");
        model.addAttribute("self", true); // 👈 PENTING

        return "admin/user_form";
    }

    @GetMapping("/me/edit")
    public String editMyProfile(HttpSession session, Model model) {

        String token = (String) session.getAttribute("JWT");
        UserDto me = authUserClient.getMe(token);

        model.addAttribute("user", me);
        model.addAttribute("mode", "edit");
        model.addAttribute("self", true); // 👈 PENTING

        return "admin/user_form";
    }

    @PostMapping("/me/edit")
    public String updateMyProfile(HttpSession session, @ModelAttribute UserDto user) {

        String token = (String) session.getAttribute("JWT");
        Long userId = user.getId();

        authUserClient.updateUser(userId, user, token);

        return "redirect:/users/me";
    }

    @GetMapping("/me/password")
    public String changeMyPasswordForm(HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT");
        UserDto me = authUserClient.getMe(token);

        model.addAttribute("password", new ChangePasswordDto());
        model.addAttribute("mode", "SELF");
        model.addAttribute("id", me.getId());
        return "admin/change_password";
    }

    @PostMapping("/me/password")
    public String changeMyPassword(HttpSession session,
            @ModelAttribute("password") ChangePasswordDto dto,
            RedirectAttributes redirect
    ) {
        String token = (String) session.getAttribute("JWT");

        authUserClient.changePasswordSelf(dto, token);
        redirect.addFlashAttribute("success", "Password berhasil diubah");
        return "redirect:/users/me";
    }

    @GetMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminChangePasswordForm(
            @PathVariable Long id,
            Model model
    ) {
        model.addAttribute("password", new ChangePasswordDto());
        model.addAttribute("id", id);
        model.addAttribute("mode", "ADMIN");
        return "admin/change_password";
    }

    @PostMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminChangePassword(HttpSession session,
            @PathVariable Long id,
            @ModelAttribute("password") ChangePasswordDto dto,
            RedirectAttributes redirect
    ) {
        String token = (String) session.getAttribute("JWT");
        authUserClient.changePasswordAdmin(id, token);
        redirect.addFlashAttribute("success", "Password user berhasil diubah");
        return "redirect:/users";
    }
}
