package com.finbridge.user.controller;

import com.finbridge.user.dto.LoginRequest;
import com.finbridge.user.dto.LoginResponse;
import com.finbridge.user.dto.RegisterRequest;
import com.finbridge.user.dto.UserDto;
import com.finbridge.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "Управление пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Регистрация пользователя",
            description = "Создаёт новый пользовательский аккаунт"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно зарегистрирован",
            content = @Content(schema = @Schema(implementation = UserDto.class))
    )
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody(
                    description = "Данные для регистрации",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody RegisterRequest request
    ) {
        try {
            UserDto user = userService.registerUser(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(
            summary = "Логин пользователя",
            description = "Аутентифицирует пользователя и возвращает JWT"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешный вход",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
    )
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody(
                    description = "Данные для входа",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest request
    ) {
        try {
            LoginResponse response = userService.loginUser(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid email or password");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(
            summary = "Получить профиль пользователя",
            description = "Возвращает профиль текущего аутентифицированного пользователя"
    )
    @ApiResponse(
            responseCode = "200",
            description = "DTO пользователя",
            content = @Content(schema = @Schema(implementation = UserDto.class))
    )
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            UserDto user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(
            summary = "Health check",
            description = "Проверка работоспособности User Service"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Сервис запущен",
            content = @Content(schema = @Schema(implementation = Map.class))
    )
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "FinBridge User Service");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
