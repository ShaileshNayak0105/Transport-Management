package com.school.busmanagement.repository;

import com.school.busmanagement.entity.Bus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Long> {

    // Helps avoid creating two bus records with the same vehicle number.
    boolean existsByBusNumber(String busNumber);

    // Used to enforce the one-driver-per-bus rule before assigning a driver.
    Optional<Bus> findByDriverId(Long driverId);
}
