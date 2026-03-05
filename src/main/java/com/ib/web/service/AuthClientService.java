package com.ib.web.service;

import com.ib.web.dto.JwtResponse;
import com.ib.web.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthClientService {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String baseUrl;

    public AuthClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String login(String username, String password) {
        try {
            LoginRequest req = new LoginRequest(username, password);
            ResponseEntity<JwtResponse> res =
                    restTemplate.postForEntity(
                            baseUrl + "/api/auth/login",
                            req,
                            JwtResponse.class
                    );
            return res.getBody().getToken();
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException("INVALID_LOGIN");
        }
    }

}