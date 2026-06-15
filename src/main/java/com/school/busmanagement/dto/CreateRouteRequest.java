package com.school.busmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Separate DTO for route creation so route form data stays decoupled
// from the Route entity and its persistence concerns.
@Data
public class CreateRouteRequest {

    @NotBlank(message = "Route name is required.")
    private String routeName;

    @NotBlank(message = "Pickup points are required.")
    private String pickupPoints;
}
