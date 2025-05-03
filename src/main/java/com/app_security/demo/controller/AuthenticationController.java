package com.app_security.demo.controller;

import com.app_security.demo.model.*;
import com.app_security.demo.repository.UserRepository;
import com.app_security.demo.util.JWTUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JWTUtil jwtUtil,
                                    UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Login endpoint to authenticate user and issue JWT
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException ex) {
            AuthResponse authResponse = new AuthResponse("");
            ApiResponse<AuthResponse> response = new ApiResponse<>(401,"Login Unsuccessful: Invalid username password",authResponse);
            return ResponseEntity.badRequest().body(response);
        }
        // Generate JWT token
        String token = jwtUtil.generateToken(authRequest.getUsername());
        AuthResponse authResponse = new AuthResponse(token);
        ApiResponse<AuthResponse> response = new ApiResponse<>(200,"Login Successful",authResponse);
        return ResponseEntity.ok(response);
    }

    // Signup endpoint for user registration
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody RegisterRequest registerRequest) {
        // Check if a user with the given username already exists
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            ApiResponse<String> response = new ApiResponse<>(400,"Username is already taken",null);
            return ResponseEntity.badRequest().body(response);
        }

        // Create a new User entity
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        // Encrypt the password before storing it
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        // Set default role (adjust as needed)
        if(null != registerRequest.getRole() && !registerRequest.getRole().isEmpty()) {
            user.setRoles(registerRequest.getRole());
        } else {
            user.setRoles("ROLE_USER");
        }
        // Save the user in the database
        userRepository.save(user);
        ApiResponse<String> response = new ApiResponse<>(200,"User registered successfully","User saved");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestHeader("Authorization") String tokenHeader) {
        try {
            String token = tokenHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);
            boolean isValid = jwtUtil.validateToken(token, username);
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(401, "Token is invalid", "false"));
            }
            return ResponseEntity.ok(new ApiResponse<>(200, "Token is valid", "true"));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(401, "Token is invalid or expired", "false"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Internal error validating token", "false"));
        }
    }
}
