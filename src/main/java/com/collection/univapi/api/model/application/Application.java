package com.collection.univapi.api.model.application;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String appId;   // public identifier

    @Column(nullable = false)
    private String apiKey;  // secret key

    private String name;    // optional: human-readable app name
}
