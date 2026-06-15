package com.school.busmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Separate DTO for the parent creation form so the UI only passes
// the fields needed to create a parent account and link a student.
@Data
public class CreateParentRequest {

    @NotBlank(message = "Parent name is required.")
    private String name;

    @Email(message = "Enter a valid email address.")
    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, message = "Password must be at least 6 characters.")
    private String password;

    private Long studentId;
}
