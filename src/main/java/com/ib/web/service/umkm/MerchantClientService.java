package com.ib.web.service.umkm;

import com.ib.web.dto.umkm.MerchantDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MerchantClientService {

    private final RestTemplate restTemplate;

    @Value("${umkm.service.url}")
    private String baseUrl;

    public MerchantClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
            throw new RuntimeException("Failed to fetch members", e);
        }
    }
}
