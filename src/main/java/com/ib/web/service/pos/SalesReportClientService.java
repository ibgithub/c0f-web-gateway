package com.ib.web.service.pos;

import com.ib.web.common.ApiResponse;
import com.ib.web.common.PageResult;
import com.ib.web.dto.pos.SalesReportDto;
import com.ib.web.dto.pos.SalesReportSummaryDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

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

    public PageResult<SalesReportDto> getSalesReportDetailPage(String jwt, int page, int size, String keyword,
              Long outletId, LocalDate salesDate) {
        ApiResponse<PageResult<SalesReportDto>> response =
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/sales/reports_sales_detail/" + outletId + "/" + salesDate)
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("keyword", keyword)
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

    public SalesReportDto getSalesReportDtoBySalesId(String token, Long salesId) {
        HttpEntity<?> entity = new HttpEntity<>(headers(token));

        return restTemplate.exchange(
                baseUrl + "/api/sales/reports_sales_detail_item/" + salesId,
                HttpMethod.GET,
                entity,
                SalesReportDto.class
        ).getBody();
    }

}
