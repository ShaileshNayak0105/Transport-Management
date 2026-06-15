package com.school.busmanagement.dto;

import lombok.Data;

// Separate response DTO for the parent dashboard so the UI receives a flat,
// read-only transport summary instead of full Student/Bus/Route entities.
@Data
public class ParentDashboardResponse {

    private String studentName;
    private String className;
    private String busNumber;
    private int busCapacity;
    private String routeName;
    private String pickupPoints;
    private String driverName;
    private String driverPhone;
}
