package com.ib.web.service.pos;

import com.ib.web.dto.pos.Sales;
import com.ib.web.dto.pos.SalesCreateRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class SalesClientService {

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

    public SalesClientService(RestTemplate restTemplate, @Qualifier("umkmWebClient") WebClient webClient) {
        this.restTemplate = restTemplate;
        this.webClient = webClient;
    }
    public Long createSales(SalesCreateRequest req, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<SalesCreateRequest> entity =
                new HttpEntity<>(req, headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        baseUrl + "/api/sales",
                        HttpMethod.POST,
                        entity,
                        Map.class
                );

        Map body = response.getBody();

        return ((Number) body.get("id")).longValue();
    }
    public Sales getById(Long id, String token) {
        HttpEntity<?> entity = new HttpEntity<>(headers(token));
        return restTemplate.exchange(
                baseUrl + "/api/sales/" + id,
                HttpMethod.GET,
                entity,
                Sales.class
        ).getBody();
    }
}
