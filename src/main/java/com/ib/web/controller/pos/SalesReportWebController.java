package com.ib.web.controller.pos;

import com.ib.web.common.PageResult;
import com.ib.web.dto.pos.SalesReportDto;
import com.ib.web.dto.pos.SalesReportSummaryDto;
import com.ib.web.service.AuthClientService;
import com.ib.web.service.JwtService;
import com.ib.web.service.pos.SalesReportClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class SalesReportWebController {

    private final AuthClientService authClientService;
    private final JwtService jwtService;
    private final SalesReportClientService salesReportClientService;

    public SalesReportWebController(AuthClientService authClientService, JwtService jwtService,
                                    SalesReportClientService salesReportClientService) {
        this.authClientService = authClientService;
        this.jwtService = jwtService;
        this.salesReportClientService = salesReportClientService;
    }
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/reports_sales")
    public String salesReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model, Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String jwt = (String) authentication.getCredentials();
        PageResult<SalesReportSummaryDto> result =
                salesReportClientService.getSalesReportSummariesPage(jwt, page, size, keyword);

        model.addAttribute("activeMenu", "reports_sales");
        model.addAttribute("salesReportSummaries", result.getContent());
        model.addAttribute("currentPage", result.getPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("pageSize", result.getSize());
        model.addAttribute("totalElements", result.getTotalElements());
        model.addAttribute("keyword", keyword == null ? "" : keyword);

        return "reports/reports_sales";
    }

    @GetMapping("/sales")
    public String salesReport(HttpSession session,
                              @RequestParam(required = false) String fromDate,
                              @RequestParam(required = false) String toDate,
                              Authentication authentication,
                              Model model
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        if(fromDate == null || fromDate.isBlank()){
            fromDate = LocalDate.now().toString();
        }

        if(toDate == null || toDate.isBlank()){
            toDate = LocalDate.now().plusDays(1).toString();
        }
        String token = (String) authentication.getCredentials();

        Long merchantId = (Long) session.getAttribute("merchantId");

        List<SalesReportDto> reports =
                salesReportClientService.getSalesReport(merchantId, fromDate, toDate, token);

        model.addAttribute("reports", reports);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "reports_sales";
    }

    @GetMapping
    public String salesReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String jwt = (String) authentication.getCredentials();

//        PageResult<MerchantDto> result =
//                salesReportClientService.getMerchantsByRolePage(jwt, page, size, keyword);

        model.addAttribute("activeMenu", "merchants");
//        model.addAttribute("merchants", result.getContent());
//        model.addAttribute("currentPage", result.getPage());
//        model.addAttribute("totalPages", result.getTotalPages());
//        model.addAttribute("pageSize", result.getSize());
//        model.addAttribute("totalElements", result.getTotalElements());
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "reports_sales";
    }

}