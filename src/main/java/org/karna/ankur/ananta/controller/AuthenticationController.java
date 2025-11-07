package org.karna.ankur.ananta.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karna.ankur.ananta.dto.AuthenticationResponse;
import org.karna.ankur.ananta.dto.LoginRequest;
import org.karna.ankur.ananta.dto.RegisterRequest;
import org.karna.ankur.ananta.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthenticationResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticationResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}
