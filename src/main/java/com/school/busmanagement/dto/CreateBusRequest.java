package com.school.busmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Separate DTO for bus creation so the form transfers only bus setup fields
// without exposing entity relationships directly to the client.
@Data
public class CreateBusRequest {

    @NotBlank(message = "Bus number is required.")
    private String busNumber;

    @NotNull(message = "Capacity is required.")
    @Min(value = 1, message = "Capacity must be at least 1.")
    private int capacity;

    @NotNull(message = "Driver selection is required.")
    private Long driverId;

    @NotNull(message = "Route selection is required.")
    private Long routeId;
}
