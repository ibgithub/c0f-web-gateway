package com.ib.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${auth.service.url}")
    private String baseUrlAuth;

    @Value("${umkm.service.url}")
    private String baseUrlUmkm;

    @Bean("authWebClient")
    public WebClient webClientAuth() {
        return WebClient.builder()
                .baseUrl(baseUrlAuth)
                .build();
    }

    @Bean("umkmWebClient")
    public WebClient webClientUmkm() {
        return WebClient.builder()
                .baseUrl(baseUrlUmkm)
                .build();
    }
}
