package com.ib.web.controller.umkm;

import com.ib.web.common.PageResult;
import com.ib.web.dto.umkm.CategoryDto;
import com.ib.web.dto.umkm.MerchantDto;
import com.ib.web.dto.umkm.ProductDto;
import com.ib.web.service.JwtService;
import com.ib.web.service.umkm.CategoryClientService;
import com.ib.web.service.umkm.MerchantClientService;
import com.ib.web.service.umkm.ProductClientService;
import com.ib.web.util.MessageUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductWebController {

    private final MerchantClientService merchantClientService;
    private final CategoryClientService categoryClientService;
    private final ProductClientService productClientService;
    private final JwtService jwtService;
    private final MessageUtil messageUtil;

    public ProductWebController(MerchantClientService merchantClientService, CategoryClientService categoryClientService,
                                ProductClientService productClientService,
                                JwtService jwtService, MessageUtil messageUtil) {
        this.merchantClientService = merchantClientService;
        this.categoryClientService = categoryClientService;
        this.productClientService = productClientService;
        this.jwtService = jwtService;
        this.messageUtil = messageUtil;
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("role")
    public String getRole(Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        Claims claims = jwtService.validateToken(jwt);
        return claims.get("role", String.class); // ADMIN / USER
    }

    @ModelAttribute("merchants")
    public List<MerchantDto> getMerchantsByRole(Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        return merchantClientService.getMerchantsByRole(jwt);
    }

    @ModelAttribute("categories")
    public List<CategoryDto> getCategoriesByRole(Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        return categoryClientService.getCategoriesByRole(jwt);
    }

    @GetMapping
    public String products(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String jwt = (String) authentication.getCredentials();

        PageResult<ProductDto> result =
                productClientService.getProducts(jwt, page, size, keyword);

        model.addAttribute("activeMenu", "merchants");
        model.addAttribute("products", result.getContent());
        model.addAttribute("currentPage", result.getPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("pageSize", result.getSize());
        model.addAttribute("totalElements", result.getTotalElements());
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "umkm/product_list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new ProductDto());
        model.addAttribute("mode", "add");
        model.addAttribute("self", false);
        return "umkm/product_form";
    }

    @PostMapping("/add")
    public String saveProduct(HttpSession session,
          @Valid @ModelAttribute("product") ProductDto product,
          BindingResult result,
          Model model, RedirectAttributes redirectAttributes
       ) {
        if (result.hasErrors()) {
            model.addAttribute("mode", "add");
            return "umkm/product_form";
        }
        String token = (String) session.getAttribute("JWT");
        productClientService.createProduct(product, token);
        redirectAttributes.addFlashAttribute(
                "success",
                messageUtil.get("product.add.success")
        );
        return "redirect:/products";
    }

    @GetMapping("/{id}")
    public String viewProduct(HttpSession session,
                           @PathVariable Long id,
                           Model model
    ) {
        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("JWT");
        ProductDto product = productClientService.getById(id, token);
        model.addAttribute("product", product);
        model.addAttribute("mode", "view");
        model.addAttribute("self", false);
        return "umkm/product_form";
    }

    @GetMapping("/{id}/edit")
    public String editProduct(HttpSession session,
                           @PathVariable Long id,
                           Model model
    ) {
        if (session.getAttribute("JWT") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("JWT");
        ProductDto product = productClientService.getById(id, token);
        model.addAttribute("product", product);
        model.addAttribute("mode", "edit");
        model.addAttribute("self", false);
        return "umkm/product_form";
    }

    @PostMapping("/{id}/edit")
    public String updateProduct(HttpSession session,
        @PathVariable Long id,
        @Valid @ModelAttribute("product") ProductDto product,
        BindingResult result,
        Model model, RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("mode", "edit");
            return "umkm/product_form";
        }
        String token = (String) session.getAttribute("JWT");

        product.setId(id);
        productClientService.updateProduct(id, product, token);
        redirectAttributes.addFlashAttribute(
                "success",
                messageUtil.get("product.edit.success")
        );
        return "redirect:/products";
    }
}
