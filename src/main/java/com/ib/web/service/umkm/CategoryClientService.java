package com.ib.web.service.umkm;

import com.ib.web.dto.umkm.CategoryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CategoryClientService {

    private final RestTemplate restTemplate;

    @Value("${umkm.service.url}")
    private String baseUrl;

    public CategoryClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders headers(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    public List<CategoryDto> getCategories(String jwt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<CategoryDto[]> res =
                    restTemplate.exchange(
                            baseUrl + "/api/categories",
                            HttpMethod.GET,
                            entity,
                            CategoryDto[].class
                    );

            return List.of(res.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch categories", e);
        }
    }

    public void createCategory(CategoryDto dto, String token) {
        HttpEntity<CategoryDto> entity =
                new HttpEntity<>(dto, headers(token));
        restTemplate.postForEntity(baseUrl + "/api/categories", entity, Void.class);
    }

    public void updateCategory(Long id, CategoryDto dto, String token) {
        HttpEntity<CategoryDto> entity =
                new HttpEntity<>(dto, headers(token));
        restTemplate.exchange(
                baseUrl + "/api/categories/" + id,
                HttpMethod.PUT,
                entity,
                Void.class
        );
    }
    public CategoryDto getById(Long id, String token) {
        HttpEntity<?> entity = new HttpEntity<>(headers(token));

        return restTemplate.exchange(
                baseUrl + "/api/categories/" + id,
                HttpMethod.GET,
                entity,
                CategoryDto.class
        ).getBody();
    }
}
