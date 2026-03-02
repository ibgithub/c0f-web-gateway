package com.ib.web.service.umkm;

import com.ib.web.common.ApiResponse;
import com.ib.web.common.PageResult;
import com.ib.web.dto.umkm.CategoryDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class CategoryClientService {

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Value("${umkm.service.url}")
    private String baseUrl;

    public CategoryClientService(RestTemplate restTemplate, @Qualifier("umkmWebClient") WebClient webClient) {
        this.restTemplate = restTemplate;
        this.webClient = webClient;
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

    public PageResult<CategoryDto> getCategories(String token, int page, int size, String keyword) {
        ApiResponse<PageResult<CategoryDto>> response =
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/categories")
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("keyword", keyword)
                                .build())
                        .headers(headers -> headers.setBearerAuth(token))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ApiResponse<PageResult<CategoryDto>>>() {})
                        .block(); // karena Thymeleaf tetap blocking

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Failed to fetch categories");
        }
        return response.getData();
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
