package com.collection.univapi.api.controller;

import com.collection.univapi.api.service.audit.AuditService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private final AuditService auditService;

    public TestController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/log")
    public String logTest() {
        auditService.log("testApp", "/test/log", "GET", 200, null);
        return "Logged an audit entry!";
    }
}
