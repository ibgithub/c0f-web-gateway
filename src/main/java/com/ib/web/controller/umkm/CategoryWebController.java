package com.ib.web.controller.umkm;

import com.ib.web.common.PageResult;
import com.ib.web.dto.umkm.CategoryDto;
import com.ib.web.service.JwtService;
import com.ib.web.service.umkm.CategoryClientService;
import com.ib.web.service.umkm.MerchantClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryWebController {
    private final MerchantClientService merchantClientService;
    private final CategoryClientService categoryClientService;
    private final JwtService jwtService;

    public CategoryWebController(MerchantClientService merchantClientService,
                                 CategoryClientService categoryClientService,
                                 JwtService jwtService) {
        this.merchantClientService = merchantClientService;
        this.categoryClientService = categoryClientService;
        this.jwtService = jwtService;
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

//    @ModelAttribute("categories")
//    public List<CategoryDto> populateCategories(Authentication authentication) {
//        String jwt = (String) authentication.getCredentials();
//        return categoryClientService.getCategories(jwt);
//    }

    @GetMapping
    public String categories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String jwt = (String) authentication.getCredentials();

        PageResult<CategoryDto> result =
                categoryClientService.getCategories(jwt, page, size, keyword);

        model.addAttribute("categories", result.getContent());
        model.addAttribute("currentPage", result.getPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("pageSize", result.getSize());
        model.addAttribute("totalElements", result.getTotalElements());
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "umkm/category_list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("category", new CategoryDto());
        model.addAttribute("mode", "add");
        model.addAttribute("self", false);
        return "umkm/category_form";
    }

    @PostMapping("/add")
    public String saveCategory(HttpSession session, @Valid @ModelAttribute("category") CategoryDto category) {
        String token = (String) session.getAttribute("JWT");
        categoryClientService.createCategory(category, token);
        return "redirect:/categories";
    }

    @GetMapping("/{id}")
    public String viewCategory(HttpSession session,
                           @PathVariable Long id,
                           Model model
    ) {
        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("JWT");
        CategoryDto category = categoryClientService.getById(id, token);
        model.addAttribute("category", category);
        model.addAttribute("mode", "view");
        model.addAttribute("self", false);
        return "umkm/category_form";
    }

    @GetMapping("/{id}/edit")
    public String editCategory(HttpSession session,
                           @PathVariable Long id,
                           Model model
    ) {
        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("JWT");
        CategoryDto category = categoryClientService.getById(id, token);
        model.addAttribute("category", category);
        model.addAttribute("mode", "edit");
        model.addAttribute("self", false);
        return "umkm/category_form";
    }

    @PostMapping("/{id}/edit")
    public String updateCategory(HttpSession session,
                             @PathVariable Long id,
                             @Valid @ModelAttribute("category") CategoryDto category
    ) {
        String token = (String) session.getAttribute("JWT");
        category.setId(id);
        categoryClientService.updateCategory(id, category, token);
        return "redirect:/categories";
    }
}
