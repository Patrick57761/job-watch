package com.jobwatch.apiservice.controllers;

import com.jobwatch.apiservice.dto.AuthResponseDTO;
import com.jobwatch.apiservice.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        return ResponseEntity.ok(authService.register(email, password));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        return ResponseEntity.ok(authService.login(email, password));
    }
}