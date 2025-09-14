package com.collection.univapi.api.service.auth;

import com.collection.univapi.api.model.application.Application;
import com.collection.univapi.api.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApiKeyAuthService {

    private final ApplicationRepository applicationRepository;

    public ApiKeyAuthService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public boolean isValid(String appId, String apiKey) {
        Optional<Application> app = applicationRepository.findbyAppIdAndApiKey(appId, apiKey);
        return app.isPresent();
    }
}
