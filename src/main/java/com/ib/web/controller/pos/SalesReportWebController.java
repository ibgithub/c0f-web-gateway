package com.ib.web.controller.pos;

import com.ib.web.common.PageResult;
import com.ib.web.dto.pos.SalesReportDto;
import com.ib.web.dto.pos.SalesReportSummaryDto;
import com.ib.web.service.AuthClientService;
import com.ib.web.service.JwtService;
import com.ib.web.service.pos.SalesReportClientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @GetMapping("/reports_sales_detail/{outletId}/{salesDate}")
    public String salesReportDetail(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model, Authentication authentication,
            @PathVariable Long outletId,
            @PathVariable LocalDate salesDate
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String jwt = (String) authentication.getCredentials();
        PageResult<SalesReportDto> result =
                salesReportClientService.getSalesReportDetailPage(jwt, page, size, keyword, outletId, salesDate);

        model.addAttribute("activeMenu", "reports_sales");
        model.addAttribute("salesReports", result.getContent());
        model.addAttribute("currentPage", result.getPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("pageSize", result.getSize());
        model.addAttribute("totalElements", result.getTotalElements());
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("outletId", outletId);
        model.addAttribute("salesDate", salesDate);

        return "reports/reports_sales_detail";
    }

}