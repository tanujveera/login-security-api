package com.app_security.demo.model;

public class AuthRequest {
    private String username;
    private String password;

    // Getters and setters (or use Lombok @Data for brevity)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

