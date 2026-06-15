package com.school.busmanagement.repository;

import com.school.busmanagement.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

// Basic CRUD repository for driver records and bus-driver assignment support.
public interface DriverRepository extends JpaRepository<Driver, Long> {
}
