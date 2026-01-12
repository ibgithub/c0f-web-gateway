package com.ib.web.service;

import com.ib.web.dto.MemberDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MemberClientService {

    private final RestTemplate restTemplate;

    public MemberClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<MemberDto> getMembers(String jwt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<MemberDto[]> res =
                    restTemplate.exchange(
                            "http://localhost:8082/api/members",
                            HttpMethod.GET,
                            entity,
                            MemberDto[].class
                    );

            return List.of(res.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch members", e);
        }
    }
}
