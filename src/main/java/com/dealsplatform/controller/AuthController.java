package com.dealsplatform.controller;

import com.dealsplatform.dto.AuthResponseDto;
import com.dealsplatform.dto.UserLoginDto;
import com.dealsplatform.dto.UserRegistrationDto;
import com.dealsplatform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "API для регистрации и входа пользователей")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", description = "Создает нового пользователя в системе")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        AuthResponseDto response = userService.registerUser(registrationDto);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Вход пользователя", description = "Аутентифицирует пользователя и возвращает JWT токен")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody UserLoginDto loginDto) {
        AuthResponseDto response = userService.loginUser(loginDto);
        return ResponseEntity.ok(response);
    }
}
