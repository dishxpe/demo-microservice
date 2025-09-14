package com.collection.univapi.api.repository;

import com.collection.univapi.api.model.application.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findbyAppIdAndApiKey(String appId, String apiKey);
}
