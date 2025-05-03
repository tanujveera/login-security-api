package com.app_security.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    // Metadata about the response
    private int status;            // HTTP status code
    private String message;        // Description or details about the response

    // The actual response data
    private T data;
}
