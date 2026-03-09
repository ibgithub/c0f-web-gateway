package com.ib.web.controller.pos;

import com.ib.web.dto.pos.SalesReportDto;
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

        return "reports/report_sales";
    }
}