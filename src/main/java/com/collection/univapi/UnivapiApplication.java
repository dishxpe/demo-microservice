package com.collection.univapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;


@SpringBootApplication
@Async
public class UnivapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnivapiApplication.class, args);
    }
}
