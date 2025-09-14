package com.collection.univapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EntityScan("com.collection.univapi.api.model")
@EnableAsync
public class UnivapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnivapiApplication.class, args);
    }
}
