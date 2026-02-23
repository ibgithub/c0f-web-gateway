package com.ib.web.controller.umkm;

import com.ib.web.dto.umkm.MerchantDto;
import com.ib.web.dto.umkm.ProductDto;
import com.ib.web.service.JwtService;
import com.ib.web.service.umkm.MerchantClientService;
import com.ib.web.service.umkm.ProductClientService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductWebController {

    private final ProductClientService productClientService;
    private final MerchantClientService merchantClientService;
    private final JwtService jwtService;

    public ProductWebController(MerchantClientService merchantClientService,
                                ProductClientService productClientService,
                                JwtService jwtService) {
        this.productClientService = productClientService;
        this.merchantClientService = merchantClientService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public String products(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String jwt = (String) authentication.getCredentials();

        Claims claims = jwtService.validateToken(jwt);

        String role = claims.get("role", String.class); // ADMIN / USER

        model.addAttribute("activeMenu", "merchants");
        model.addAttribute("title", "Merchants");
        model.addAttribute("role", role);
        List<ProductDto> products = productClientService.getProducts(jwt);
        model.addAttribute("products", products);

        return "umkm/product_list";
    }

    @GetMapping("/add")
    public String addForm(Model model, Authentication authentication) {
        String jwt = (String) authentication.getCredentials();
        List<MerchantDto> merchants = merchantClientService.getMerchants(jwt);

        model.addAttribute("merchants", merchants);
        model.addAttribute("product", new ProductDto());
        model.addAttribute("mode", "add");
        model.addAttribute("self", false);
        return "umkm/product_form";
    }

    @PostMapping("/add")
    public String saveUser(HttpSession session, ProductDto product) {
        String token = (String) session.getAttribute("JWT");
        productClientService.createProduct(product, token);
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
        List<MerchantDto> merchants = merchantClientService.getMerchants(token);

        model.addAttribute("merchants", merchants);
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
        List<MerchantDto> merchants = merchantClientService.getMerchants(token);

        model.addAttribute("merchants", merchants);
        model.addAttribute("product", product);
        model.addAttribute("mode", "edit");
        model.addAttribute("self", false);
        return "umkm/product_form";
    }

    @PostMapping("/{id}/edit")
    public String updateProduct(HttpSession session,
                             @PathVariable Long id,
                             @ModelAttribute ProductDto product
    ) {
        String token = (String) session.getAttribute("JWT");

        product.setId(id);
        productClientService.updateProduct(id, product, token);
        return "redirect:/products";
    }
}
