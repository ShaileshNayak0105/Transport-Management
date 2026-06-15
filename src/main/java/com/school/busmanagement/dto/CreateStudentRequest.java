package com.school.busmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Separate DTO for student creation so the frontend sends only form data
// and does not expose JPA relationships or entity internals.
@Data
public class CreateStudentRequest {

    @NotBlank(message = "Student name is required.")
    private String name;

    @NotNull(message = "Age is required.")
    @Min(value = 1, message = "Age must be at least 1.")
    private int age;

    @NotBlank(message = "Class name is required.")
    private String className;

    @NotNull(message = "Bus selection is required.")
    private Long busId;

    @NotNull(message = "Route selection is required.")
    private Long routeId;
}
