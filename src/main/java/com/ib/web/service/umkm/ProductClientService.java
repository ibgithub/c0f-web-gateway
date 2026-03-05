package com.ib.web.service.umkm;

import com.ib.web.common.ApiResponse;
import com.ib.web.common.PageResult;
import com.ib.web.dto.umkm.ProductDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ProductClientService {

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Value("${umkm.service.url}")
    private String baseUrl;

    public ProductClientService(RestTemplate restTemplate, @Qualifier("umkmWebClient") WebClient webClient) {
        this.restTemplate = restTemplate;
        this.webClient = webClient;
    }

    private HttpHeaders headers(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    public List<ProductDto> getProducts(String jwt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<ProductDto[]> res =
                    restTemplate.exchange(
                            baseUrl + "/api/products",
                            HttpMethod.GET,
                            entity,
                            ProductDto[].class
                    );

            return List.of(res.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch members", e);
        }
    }

    public PageResult<ProductDto> getProducts(String token, int page, int size, String keyword) {
        ApiResponse<PageResult<ProductDto>> response =
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/products")
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("keyword", keyword)
                                .build())
                        .headers(headers -> headers.setBearerAuth(token))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ApiResponse<PageResult<ProductDto>>>() {})
                        .block(); // karena Thymeleaf tetap blocking

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Failed to fetch categories");
        }
        return response.getData();
    }

    public void createProduct(ProductDto dto, String token) {
        HttpEntity<ProductDto> entity =
                new HttpEntity<>(dto, headers(token));
        restTemplate.postForEntity(baseUrl + "/api/products", entity, Void.class);
    }

    public void updateProduct(Long id, ProductDto dto, String token) {
        HttpEntity<ProductDto> entity =
                new HttpEntity<>(dto, headers(token));
        restTemplate.exchange(
                baseUrl + "/api/products/" + id,
                HttpMethod.PUT,
                entity,
                Void.class
        );
    }
    public ProductDto getById(Long id, String token) {
        HttpEntity<?> entity = new HttpEntity<>(headers(token));

        return restTemplate.exchange(
                baseUrl + "/api/products/" + id,
                HttpMethod.GET,
                entity,
                ProductDto.class
        ).getBody();
    }
}
