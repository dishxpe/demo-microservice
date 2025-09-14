package com.collection.univapi.api.config;

import com.collection.univapi.api.service.auth.ApiKeyAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private final ApiKeyAuthService authService;

    public ApiKeyInterceptor(ApiKeyAuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String appId = request.getHeader("X-App-Id");
        String apiKey = request.getHeader("X-Api-Key");

        if (appId == null || apiKey == null || !authService.isValid(appId, apiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String json = """
            {
                "timestamp": "%s",
                "status": 401,
                "error": "Unauthorized",
                "message": "Invalid AppId or ApiKey",
                "path": "%s"
            }
            """.formatted(java.time.Instant.now(), request.getRequestURI());

            response.getWriter().write(json);
            return false;
        }

        return true;
    }

}
