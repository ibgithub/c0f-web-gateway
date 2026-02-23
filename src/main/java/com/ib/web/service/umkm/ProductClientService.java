package com.ib.web.service.umkm;

import com.ib.web.dto.umkm.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductClientService {

    private final RestTemplate restTemplate;

    @Value("${umkm.service.url}")
    private String baseUrl;

    public ProductClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
