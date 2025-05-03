package com.app_security.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
//@ConfigurationProperties
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppProperties {
    @Value("${jwt.secret}")
    private String jwtSecret;
}
