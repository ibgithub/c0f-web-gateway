package com.ib.web.service;

import com.ib.web.dto.ChangePasswordDto;
import com.ib.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class AuthUserClient {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String baseUrl;

    public AuthUserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders headers(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    public List<UserDto> getUsers(String token) {
        HttpEntity<?> entity = new HttpEntity<>(headers(token));
        ResponseEntity<UserDto[]> res =
                restTemplate.exchange(
                        baseUrl + "/api/users",
                        HttpMethod.GET,
                        entity,
                        UserDto[].class
                );
        return Arrays.asList(res.getBody());
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
