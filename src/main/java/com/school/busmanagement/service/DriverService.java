package com.school.busmanagement.service;

import com.school.busmanagement.dto.CreateDriverRequest;
import com.school.busmanagement.entity.Driver;
import com.school.busmanagement.exception.ResourceNotFoundException;
import com.school.busmanagement.repository.DriverRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public long countDrivers() {
        return driverRepository.count();
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
    }

    @Transactional
    public Driver createDriver(CreateDriverRequest request) {
        validateDriverPhone(request.getPhone());

        Driver driver = Driver.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .build();

        return driverRepository.save(driver);
    }

    @Transactional
    public Driver updateDriver(Long id, CreateDriverRequest request) {
        validateDriverPhone(request.getPhone());

        Driver driver = getDriverById(id);
        driver.setName(request.getName());
        driver.setPhone(request.getPhone());
        return driverRepository.save(driver);
    }

    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = getDriverById(id);
        driverRepository.delete(driver);
    }

    private void validateDriverPhone(String phone) {
        if (phone == null || !phone.matches("\\d{10}")) {
            throw new IllegalArgumentException("Driver phone must be 10 digits.");
        }
    }
}
