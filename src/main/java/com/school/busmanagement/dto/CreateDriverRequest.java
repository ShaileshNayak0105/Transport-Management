package com.school.busmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

// Separate DTO for driver creation so the frontend submits only the fields
// required by the admin form instead of the full entity object.
@Data
public class CreateDriverRequest {

    @NotBlank(message = "Driver name is required.")
    private String name;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits.")
    private String phone;
}
