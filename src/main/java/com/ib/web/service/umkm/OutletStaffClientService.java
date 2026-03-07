package com.ib.web.service.umkm;

import com.ib.web.common.ApiResponse;
import com.ib.web.common.PageResult;
import com.ib.web.dto.umkm.OutletStaffDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class OutletStaffClientService {

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Value("${umkm.service.url}")
    private String baseUrl;

    public OutletStaffClientService(RestTemplate restTemplate, @Qualifier("umkmWebClient") WebClient webClient) {
        this.restTemplate = restTemplate;
        this.webClient = webClient;
    }

    private HttpHeaders headers(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    public List<OutletStaffDto> getOutletStaffs(String token) {
        try {
            HttpEntity<?> entity = new HttpEntity<>(headers(token));
            ResponseEntity<OutletStaffDto[]> res =
                    restTemplate.exchange(
                            baseUrl + "/api/outletStaffs",
                            HttpMethod.GET,
                            entity,
                            OutletStaffDto[].class
                    );

            return List.of(res.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch outlets", e);
        }
    }

//    public List<OutletStaffDto> getOutletStaffsByRole(String token) {
//        try {
//            HttpEntity<?> entity = new HttpEntity<>(headers(token));
//            ResponseEntity<OutletDto[]> res =
//                    restTemplate.exchange(
//                            baseUrl + "/api/outlets/data",
//                            HttpMethod.GET,
//                            entity,
//                            OutletDto[].class
//                    );
//
//            return List.of(res.getBody());
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch outlets", e);
//        }
//    }

//    public List<OutletDto> getOutletsByMerchant(Long merchantId, String token) {
//        try {
//            HttpEntity<?> entity = new HttpEntity<>(headers(token));
//            ResponseEntity<OutletDto[]> res =
//                    restTemplate.exchange(
//                            baseUrl + "/api/outlets/data",
//                            HttpMethod.GET,
//                            entity,
//                            OutletDto[].class
//                    );
//
//            return List.of(res.getBody());
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch outlets", e);
//        }
//    }

    public PageResult<OutletStaffDto> getOutletStaffs(String token, int page, int size, String keyword) {
        ApiResponse<PageResult<OutletStaffDto>> response =
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/outlet-staffs")
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("keyword", keyword)
                                .build())
                        .headers(headers -> headers.setBearerAuth(token))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ApiResponse<PageResult<OutletStaffDto>>>() {})
                        .block(); // karena Thymeleaf tetap blocking

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Failed to fetch outlets");
        }
        return response.getData();
    }

    public void createOutletStaff(OutletStaffDto dto, String token) {
        HttpEntity<?> entity = new HttpEntity<>(dto, headers(token));
        restTemplate.postForEntity(baseUrl + "/api/outlet-staffs", entity, Void.class);
    }

    public void updateOutletStaff(Long id, OutletStaffDto dto, String token) {
        HttpEntity<?> entity = new HttpEntity<>(dto, headers(token));
        restTemplate.exchange(
                baseUrl + "/api/outlet-staffs/" + id,
                HttpMethod.PUT,
                entity,
                Void.class
        );
    }
    public OutletStaffDto getById(Long id, String token) {
        HttpEntity<?> entity = new HttpEntity<>(headers(token));
        return restTemplate.exchange(
                baseUrl + "/api/outlet-staffs/" + id,
                HttpMethod.GET,
                entity,
                OutletStaffDto.class
        ).getBody();
    }
}
