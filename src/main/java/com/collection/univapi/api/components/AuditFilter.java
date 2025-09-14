package com.collection.univapi.api.components;

import com.collection.univapi.api.service.audit.AuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuditFilter extends OncePerRequestFilter {

    private final AuditService auditService;

    public AuditFilter(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String appId = request.getHeader("X-App-Id");
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        try {
            filterChain.doFilter(request, response);
            int statusCode = response.getStatus();
            auditService.log(appId, endpoint, method, statusCode, null);
        } catch (Exception e) {
            auditService.log(appId, endpoint, method, 500, e.getMessage());
            throw e;
        }
    }
}
