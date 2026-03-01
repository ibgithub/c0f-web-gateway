package com.ib.web.service.umkm;

import com.ib.web.common.ApiResponse;
import com.ib.web.common.PageResult;
import com.ib.web.dto.umkm.MerchantDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class MerchantClientService {

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Value("${umkm.service.url}")
    private String baseUrl;

    public MerchantClientService(RestTemplate restTemplate, @Qualifier("umkmWebClient") WebClient webClient) {
        this.restTemplate = restTemplate;
        this.webClient = webClient;
    }

    private HttpHeaders headers(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    public List<MerchantDto> getMerchants(String jwt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<MerchantDto[]> res =
                    restTemplate.exchange(
                            baseUrl + "/api/merchants",
                            HttpMethod.GET,
                            entity,
                            MerchantDto[].class
                    );

            return List.of(res.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch merchants", e);
        }
    }

    public PageResult<MerchantDto> getMerchants(String jwt, int page, int size) {

        ApiResponse<PageResult<MerchantDto>> response =
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/merchants")
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .build())
                        .headers(headers -> headers.setBearerAuth(jwt))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ApiResponse<PageResult<MerchantDto>>>() {})
                        .block(); // karena Thymeleaf tetap blocking

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Failed to fetch merchants");
        }

        return response.getData();
    }

    public void createMerchant(MerchantDto dto, String token) {
        HttpEntity<MerchantDto> entity =
                new HttpEntity<>(dto, headers(token));
        restTemplate.postForEntity(baseUrl + "/api/merchants", entity, Void.class);
    }

    public void updateMerchant(Long id, MerchantDto dto, String token) {
        HttpEntity<MerchantDto> entity =
                new HttpEntity<>(dto, headers(token));
        restTemplate.exchange(
                baseUrl + "/api/merchants/" + id,
                HttpMethod.PUT,
                entity,
                Void.class
        );
    }
    public MerchantDto getById(Long id, String token) {
        HttpEntity<?> entity = new HttpEntity<>(headers(token));

        return restTemplate.exchange(
                baseUrl + "/api/merchants/" + id,
                HttpMethod.GET,
                entity,
                MerchantDto.class
        ).getBody();
    }

    // DELETE merchant  ← BARU
    public void deleteMerchant(Long id, String token) {
//        String url = baseUrl + "/merchants/" + id;
//        HttpEntity<Void> entity = new HttpEntity<>(bearerHeaders(token));
//        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

}
