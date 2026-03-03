package com.ib.web.service;

import com.ib.web.common.ApiResponse;
import com.ib.web.common.PageResult;
import com.ib.web.dto.ChangePasswordDto;
import com.ib.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Service
public class AuthUserClient {

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Value("${auth.service.url}")
    private String baseUrl;

    public AuthUserClient(RestTemplate restTemplate, @Qualifier("authWebClient") WebClient webClient) {
        this.restTemplate = restTemplate;
        this.webClient = webClient;
    }

    private HttpHeaders headers(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

//    public List<UserDto> getUsers(String token) {
//        HttpEntity<?> entity = new HttpEntity<>(headers(token));
//        ResponseEntity<UserDto[]> res =
//                restTemplate.exchange(
//                        baseUrl + "/api/users",
//                        HttpMethod.GET,
//                        entity,
//                        UserDto[].class
//                );
//        return Arrays.asList(res.getBody());
//    }

    public PageResult<UserDto> getUsers(String token, int page, int size, String keyword) {
        ApiResponse<PageResult<UserDto>> response =
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/users")
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("keyword", keyword)
                                .build())
                        .headers(headers -> headers.setBearerAuth(token))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ApiResponse<PageResult<UserDto>>>() {})
                        .block(); // karena Thymeleaf tetap blocking

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Failed to fetch merchants");
        }

        return response.getData();
    }

    public List<UserDto> getUsersByRole(String token, String role) {
        HttpEntity<?> entity = new HttpEntity<>(headers(token));
        ResponseEntity<UserDto[]> res =
                restTemplate.exchange(
                        baseUrl + "/api/users/byRole/" + role,
                        HttpMethod.GET,
                        entity,
                        UserDto[].class
                );
        return Arrays.asList(res.getBody());
    }

    public UserDto getMe(String token) {
        HttpEntity<?> entity = new HttpEntity<>(headers(token));
        return restTemplate.exchange(
                baseUrl + "/api/users/me",
                HttpMethod.GET,
                entity,
                UserDto.class
        ).getBody();
    }

    public void createUser(UserDto dto, String token) {
        HttpEntity<UserDto> entity =
                new HttpEntity<>(dto, headers(token));
        restTemplate.postForEntity(baseUrl + "/api/users", entity, Void.class);
    }

    public void updateUser(Long id, UserDto dto, String token) {
        HttpEntity<UserDto> entity =
                new HttpEntity<>(dto, headers(token));
        restTemplate.exchange(
                baseUrl + "/api/users/" + id,
                HttpMethod.PUT,
                entity,
                Void.class
        );
    }
    public UserDto getById(Long id, String token) {
        HttpEntity<?> entity = new HttpEntity<>(headers(token));

        return restTemplate.exchange(
                baseUrl + "/api/users/" + id,
                HttpMethod.GET,
                entity,
                UserDto.class
        ).getBody();
    }
    public ChangePasswordDto changePasswordSelf(ChangePasswordDto dto, String token) {
        HttpEntity<ChangePasswordDto> entity =
                new HttpEntity<>(dto, headers(token));
        return restTemplate.exchange(
                baseUrl + "/api/users/me/password",
                HttpMethod.PUT,
                entity,
                ChangePasswordDto.class
        ).getBody();
    }
    public ChangePasswordDto changePasswordAdmin(Long id, ChangePasswordDto dto, String token) {
        HttpEntity<ChangePasswordDto> entity =
                new HttpEntity<>(dto, headers(token));

        return restTemplate.exchange(
                baseUrl + "/api/users/" + id + "/password",
                HttpMethod.PUT,
                entity,
                ChangePasswordDto.class
        ).getBody();
    }
}
