package kr.co.solpick.api.external.point.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    @Value("${solpick.api.key}")
    private String validApiKey;

    public boolean validateApiKey(String apiKey) {
        return validApiKey.equals(apiKey);
    }
}