package com.finbridge.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Mono<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "FinBridge Gateway");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("version", "1.0.0");
        return Mono.just(response);
    }

    @GetMapping("/")
    public Mono<Map<String, String>> root() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to FinBridge Platform!");
        response.put("documentation", "/swagger-ui.html");
        response.put("health", "/health");
        return Mono.just(response);
    }
}
