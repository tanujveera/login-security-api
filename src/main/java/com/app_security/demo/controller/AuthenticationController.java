package com.app_security.demo.controller;

import com.app_security.demo.model.AuthRequest;
import com.app_security.demo.model.AuthResponse;
import com.app_security.demo.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // Login endpoint to authenticate user and issue JWT
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        }
        // Generate JWT token
        String token = jwtUtil.generateToken(authRequest.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
