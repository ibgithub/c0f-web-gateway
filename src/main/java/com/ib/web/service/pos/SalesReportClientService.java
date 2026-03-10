package com.ib.web.service.pos;

import com.ib.web.common.ApiResponse;
import com.ib.web.common.PageResult;
import com.ib.web.dto.pos.SalesReportDto;
import com.ib.web.dto.pos.SalesReportSummaryDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class SalesReportClientService {

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Value("${umkm.service.url}")
    private String baseUrl;

    private HttpHeaders headers(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    public SalesReportClientService(RestTemplate restTemplate, @Qualifier("umkmWebClient") WebClient webClient) {
        this.restTemplate = restTemplate;
        this.webClient = webClient;
    }

    public List<SalesReportDto> getSalesReport(Long merchantId,
            String fromDate,
            String toDate,
            String token
    ){

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        String url;
        if (merchantId == null) {
            url = baseUrl +
                    "/api/sales/report?fromDate=" + fromDate +
                    "&toDate=" + toDate;
        } else {
            url = baseUrl +
                    "/api/sales/report?merchantId=" + merchantId +
                    "&fromDate=" + fromDate +
                    "&toDate=" + toDate;
        }
        ResponseEntity<List<SalesReportDto>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<SalesReportDto>>() {}
                );

        return response.getBody();
    }

    public PageResult<SalesReportSummaryDto> getSalesReportSummariesPage(String jwt, int page, int size, String keyword) {
        ApiResponse<PageResult<SalesReportSummaryDto>> response =
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/sales/reports_sales")
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("keyword", keyword)
                                .build())
                        .headers(headers -> headers.setBearerAuth(jwt))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ApiResponse<PageResult<SalesReportSummaryDto>>>() {})
                        .block(); // karena Thymeleaf tetap blocking

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Failed to fetch merchants");
        }

        return response.getData();
    }

    public PageResult<SalesReportDto> getSalesReportsPage(String jwt, int page, int size, String keyword,Long merchantId,
                                                          String fromDate,
                                                          String toDate) {
        ApiResponse<PageResult<SalesReportDto>> response =
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/sales/report")
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("keyword", keyword)
                                .queryParam("merchantId", merchantId)
                                .queryParam("fromDate", fromDate)
                                .queryParam("toDate", toDate)
                                .build())
                        .headers(headers -> headers.setBearerAuth(jwt))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ApiResponse<PageResult<SalesReportDto>>>() {})
                        .block(); // karena Thymeleaf tetap blocking

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Failed to fetch merchants");
        }

        return response.getData();
    }

}
