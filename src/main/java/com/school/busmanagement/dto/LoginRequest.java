package com.school.busmanagement.dto;

import lombok.Data;

// Separate login DTO so the form only sends authentication fields
// instead of exposing the full User entity structure.
@Data
public class LoginRequest {

    private String email;
    private String password;
}
